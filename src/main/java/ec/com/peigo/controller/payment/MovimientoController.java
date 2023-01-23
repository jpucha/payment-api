/**
 * 
 */
package ec.com.peigo.controller.payment;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import ec.com.peigo.controller.payment.dto.PagoEntradaDto;
import ec.com.peigo.controller.payment.dto.PagoSalidaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.com.peigo.controller.payment.dto.MovimientoEntradaDto;
import ec.com.peigo.enumeration.TipoMovimientoEnum;
import ec.com.peigo.model.payment.Cliente;
import ec.com.peigo.model.payment.Cuenta;
import ec.com.peigo.model.payment.Movimiento;
import ec.com.peigo.service.payment.ClienteService;
import ec.com.peigo.service.payment.CuentaService;
import ec.com.peigo.service.payment.MovimientoService;

/**
 * 
 * <b> Clase controlador para los movimientos contables. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@RestController
@RequestMapping("/api/paymentTransaction")
public class MovimientoController {

	private static final Logger log = LoggerFactory.getLogger(MovimientoController.class);

	private static final int limiteDiario = 1000;

	@Autowired
	private MovimientoService movimientoService;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private CuentaService cuentaService;

	/**
	 * 
	 * <b> Metodo que crea los movimientos. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param pagoEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@PostMapping
	public ResponseEntity<?> create(@Validated @RequestBody PagoEntradaDto pagoEntradaDto) {

		try {
			//valido que los datos de entrada de cuenta origen y cuenta destino no sean vacios o nulos
			if (ObjectUtils.isEmpty(pagoEntradaDto.getCuentaOrigen()) ||  ObjectUtils.isEmpty(pagoEntradaDto.getCuentaDestino())
					|| (BigDecimal.ZERO == pagoEntradaDto.getSaldo() )) {
				return new ResponseEntity<>("Cuenta origen, cuenta destino, saldo son obligatorios.",
						HttpStatus.BAD_REQUEST);
			}
			// valido que el saldo no sean vacio o nulo y que sea un saldo positivo
			if (pagoEntradaDto.getSaldo().compareTo(BigDecimal.ZERO) <= 0) {
				return new ResponseEntity<>("Saldo no puede ser cero o negativo.",
						HttpStatus.BAD_REQUEST);
			}

			Optional<Cuenta> cuentaOrigen = cuentaService.obtenerPorNumeroCuenta(pagoEntradaDto.getCuentaOrigen());
			if (ObjectUtils.isEmpty(cuentaOrigen)) {
				return new ResponseEntity<>("La cuenta origen proporcionada no existe.", HttpStatus.BAD_REQUEST);
			}
			//Validacion que este dentro del limite diario
			Double sumaDiariaDebito = movimientoService.obtenerSumaValorClienteCuentaFecha(cuentaOrigen.get().getIdCliente(),
					cuentaOrigen.get().getIdCuenta(), TipoMovimientoEnum.DEBITO.getDescripcion(), new Date());
			if (limiteDiario <= sumaDiariaDebito.doubleValue() + pagoEntradaDto.getSaldo().doubleValue()) {
				return new ResponseEntity<>("Cupo diario Excedido", HttpStatus.BAD_REQUEST);
			}
			//Valido que la cuenta origen tenga saldo
			if (BigDecimal.ZERO.compareTo(cuentaOrigen.get().getSaldoInicial()) == 0) {
				return new ResponseEntity<>("Saldo no disponible", HttpStatus.BAD_REQUEST);
			}
			//valido que el movimiento a debitar sea menor o igual que el saldo que se tiene en la cuenta
			if (pagoEntradaDto.getSaldo().compareTo(cuentaOrigen.get().getSaldoInicial()) > 0) {
				return new ResponseEntity<>("Saldo no disponible", HttpStatus.BAD_REQUEST);
			}

			Optional<Cuenta> cuentaDestino = cuentaService.obtenerPorNumeroCuenta(pagoEntradaDto.getCuentaDestino());
			if (ObjectUtils.isEmpty(cuentaDestino)) {
				return new ResponseEntity<>("La cuenta destino proporcionada no existe.", HttpStatus.BAD_REQUEST);
			}

			Optional<Cliente> clienteOrigen = clienteService.obtenerPorId(cuentaOrigen.get().getIdCliente());
			if (ObjectUtils.isEmpty(clienteOrigen)) {
				return new ResponseEntity<>("El cliente origen no existe para el id de cuenta consultado.", HttpStatus.BAD_REQUEST);
			}

			Optional<Cliente> clienteDestino = clienteService.obtenerPorId(cuentaDestino.get().getIdCliente());
			if (ObjectUtils.isEmpty(clienteDestino)) {
				return new ResponseEntity<>("El cliente destino no existe para el id de cuenta consultado.", HttpStatus.BAD_REQUEST);
			}

			Movimiento debito = new Movimiento();
			debito.setIdCliente(clienteOrigen.get().getClienteId());
			debito.setIdCuenta(cuentaOrigen.get().getIdCuenta());
			debito.setNumeroTransaccion(1000L);
			debito.setValor(pagoEntradaDto.getSaldo());
			debito.setTipoMovimiento(TipoMovimientoEnum.DEBITO.getDescripcion());
			debito.setSaldoAnterior(cuentaOrigen.get().getSaldoInicial());
			debito.setSaldo(debito.getSaldoAnterior().subtract(debito.getValor()));
			debito.setFecha(new Date());
			//Guardo el debito
			Movimiento movimientoOrigenGuardado = movimientoService.create(debito);
			//Actualizar cuenta origen
			cuentaOrigen.get().setSaldoInicial(debito.getSaldo());
			cuentaService.update(cuentaOrigen.get());

			Movimiento credito = new Movimiento();
			credito.setIdCliente(clienteDestino.get().getClienteId());
			credito.setIdCuenta(cuentaDestino.get().getIdCuenta());
			credito.setNumeroTransaccion(1000L);
			credito.setValor(pagoEntradaDto.getSaldo());
			credito.setTipoMovimiento(TipoMovimientoEnum.CREDITO.getDescripcion());
			credito.setSaldoAnterior(cuentaDestino.get().getSaldoInicial());
			credito.setSaldo(credito.getSaldoAnterior().add(credito.getValor()));
			credito.setFecha(new Date());
			//Guardo el credito
			Movimiento movimientoDestinoGuardado = movimientoService.create(credito);
			//Actualizar cuanta destino
			cuentaDestino.get().setSaldoInicial(credito.getSaldo());
			cuentaService.update(cuentaDestino.get());
			PagoSalidaDto resultado = new PagoSalidaDto();
			resultado.setNumeroOperacion(movimientoDestinoGuardado.getNumeroTransaccion());
			resultado.setSaldoCuentaOrigen(movimientoOrigenGuardado.getSaldo());
			resultado.setSaldoCuentaDestino(movimientoDestinoGuardado.getSaldo());
			return new ResponseEntity<PagoSalidaDto>(resultado, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * <b> Metodo para obtiene los movimientos del cliente por su cuenta. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param movimientoEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@GetMapping
	public ResponseEntity<?> obtenerPorClienteCuenta(
			@Validated @RequestBody MovimientoEntradaDto movimientoEntradaDto) {
		try {

			if (ObjectUtils.isEmpty(movimientoEntradaDto.getIdentificacion())
					|| ObjectUtils.isEmpty(movimientoEntradaDto.getNumeroCuenta())) {
				return new ResponseEntity<>("La identificación y el número de cuenta son obligatorios",
						HttpStatus.BAD_REQUEST);
			}
			Optional<Cliente> cliente = clienteService
					.obtenerPorIdentificacion(movimientoEntradaDto.getIdentificacion());
			if (ObjectUtils.isEmpty(cliente)) {
				return new ResponseEntity<>("El cliente no existe con la identificación", HttpStatus.BAD_REQUEST);
			}
			Optional<Cuenta> cuenta = cuentaService.obtenerPorNumeroCuenta(movimientoEntradaDto.getNumeroCuenta());
			if (ObjectUtils.isEmpty(cuenta)) {
				return new ResponseEntity<>("La cuenta no existe con el número de cuenta", HttpStatus.BAD_REQUEST);
			}

			List<Movimiento> listadoMovimiento = movimientoService.obtenerPorClienteCuenta(cliente.get().getClienteId(),
					cuenta.get().getIdCuenta());
			if (null == listadoMovimiento || listadoMovimiento.isEmpty()) {
				return new ResponseEntity<>("No existen movimientos con lo parametros indicados.", HttpStatus.OK);
			} else {
				return new ResponseEntity<List<Movimiento>>(listadoMovimiento, HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * <b> Metodo que actualiza el moviemto. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param movimientoEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@PutMapping
	public ResponseEntity<?> update(@Validated @RequestBody MovimientoEntradaDto movimientoEntradaDto) {
		try {
			if (ObjectUtils.isEmpty(movimientoEntradaDto.getIdMovimiento())) {
				return new ResponseEntity<>("El id del movimiento es obligatorio", HttpStatus.BAD_REQUEST);
			}
			Optional<Movimiento> movimientoEncontrado = movimientoService.obtenerPorId(movimientoEntradaDto.getIdMovimiento());
			if (movimientoEncontrado.isPresent()) {
				Movimiento movimiento = movimientoEncontrado.get();
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				Date date = formatter.parse(movimientoEntradaDto.getFecha());
				movimiento.setFecha(date);
				Movimiento movimientoGuardado = movimientoService.update(movimiento);
				return new ResponseEntity<Movimiento>(movimientoGuardado, HttpStatus.OK);
			}
			return new ResponseEntity<>("No se encuentra en movimiento", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * <b> Metodo que elimina un registro por su id. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param id
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		try {

			if (ObjectUtils.isEmpty(id)) {
				return new ResponseEntity<>("El id del movimiento es obligatorio", HttpStatus.BAD_REQUEST);
			}
			Optional<Movimiento> movimientoEncontrado = movimientoService.obtenerPorId(id);
			if (movimientoEncontrado.isPresent()) {
				movimientoService.delete(id);
				return new ResponseEntity<>("Registro Eliminado", HttpStatus.OK);
			}
			return new ResponseEntity<String>(
					"No se puede eliminar el movimiento con id: " + id + " no existe el registro",
					HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
