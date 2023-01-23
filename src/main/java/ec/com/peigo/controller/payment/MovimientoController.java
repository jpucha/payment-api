/**
 * 
 */
package ec.com.peigo.controller.payment;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
@RequestMapping("/api/movimientos")
public class MovimientoController {

	private static final Logger log = LoggerFactory.getLogger(MovimientoController.class);

	private static final int limiteDiario = 1000;

	@Autowired
	private MovimientoService service;

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
	 * @param movimientoEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@PostMapping
	public ResponseEntity<?> create(@Validated @RequestBody MovimientoEntradaDto movimientoEntradaDto) {

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
			Movimiento movimiento = new Movimiento();
			movimiento.setIdCliente(cliente.get().getClienteId());
			movimiento.setIdCuenta(cuenta.get().getIdCuenta());
			BigDecimal saldo;
			movimientoEntradaDto.setValor(Math.abs(movimientoEntradaDto.getValor()));
			if (TipoMovimientoEnum.CREDITO.getDescripcion()
					.equalsIgnoreCase(movimientoEntradaDto.getTipoMovimiento())) {
				saldo = cuenta.get().getSaldoInicial().add(BigDecimal.valueOf(movimientoEntradaDto.getValor()));
			} else if (TipoMovimientoEnum.DEBITO.getDescripcion()
					.equalsIgnoreCase(movimientoEntradaDto.getTipoMovimiento())) {
				if (BigDecimal.ZERO.compareTo(cuenta.get().getSaldoInicial()) == 0) {
					return new ResponseEntity<>("Saldo no disponible", HttpStatus.BAD_REQUEST);
				}
				Double sumaDiariaDebito = service.obtenerSumaValorClienteCuentaFecha(cliente.get().getClienteId(),
						cuenta.get().getIdCuenta(), TipoMovimientoEnum.DEBITO.getDescripcion(), new Date());
				if (limiteDiario <= sumaDiariaDebito.doubleValue() + movimientoEntradaDto.getValor()) {
					return new ResponseEntity<>("Cupo diario Excedido", HttpStatus.BAD_REQUEST);
				}
				saldo = cuenta.get().getSaldoInicial().subtract(BigDecimal.valueOf(movimientoEntradaDto.getValor()));
				movimientoEntradaDto.setValor(-movimientoEntradaDto.getValor());

			} else {
				return new ResponseEntity<>("Tipo de movimiento no encontrado", HttpStatus.BAD_REQUEST);
			}

			movimiento.setSaldoAnterior(cuenta.get().getSaldoInicial());
			movimiento.setSaldo(saldo);
			movimiento.setTipoMovimiento(movimientoEntradaDto.getTipoMovimiento());
			movimiento.setFecha(new Date());
			movimiento.setValor(BigDecimal.valueOf(movimientoEntradaDto.getValor()));
			Movimiento movimientoGuardado = service.create(movimiento);
			cuenta.get().setSaldoInicial(saldo);
			cuentaService.update(cuenta.get());
			return new ResponseEntity<Movimiento>(movimientoGuardado, HttpStatus.CREATED);
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

			List<Movimiento> listadoMovimiento = service.obtenerPorClienteCuenta(cliente.get().getClienteId(),
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
			Optional<Movimiento> movimientoEncontrado = service.obtenerPorId(movimientoEntradaDto.getIdMovimiento());
			if (movimientoEncontrado.isPresent()) {
				Movimiento movimiento = movimientoEncontrado.get();
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

				Date date = formatter.parse(movimientoEntradaDto.getFecha());
				movimiento.setFecha(date);
				Movimiento movimientoGuardado = service.update(movimiento);
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
			Optional<Movimiento> movimientoEncontrado = service.obtenerPorId(id);
			if (movimientoEncontrado.isPresent()) {
				service.delete(id);
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
