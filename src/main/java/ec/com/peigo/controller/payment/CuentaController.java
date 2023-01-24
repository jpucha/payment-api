/**
 * 
 */
package ec.com.peigo.controller.payment;

import java.math.BigDecimal;
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

import ec.com.peigo.controller.payment.dto.CuentaEntradaDto;
import ec.com.peigo.enumeration.EstadoEmun;
import ec.com.peigo.model.payment.Cliente;
import ec.com.peigo.model.payment.Cuenta;
import ec.com.peigo.service.payment.ClienteService;
import ec.com.peigo.service.payment.CuentaService;

/**
 * 
 * <b> Clase controlador de las cuentas. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

	private static final Logger log = LoggerFactory.getLogger(CuentaController.class);

	@Autowired
	private CuentaService service;

	@Autowired
	private ClienteService clienteService;

	/**
	 * 
	 * <b> Metodo que crea cuentas. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param cuentaEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@PostMapping
	public ResponseEntity<?> create(@Validated @RequestBody CuentaEntradaDto cuentaEntradaDto) {
		try {

			Optional<Cliente> clienteEncontrado = null;
			if (!ObjectUtils.isEmpty(cuentaEntradaDto.getIdentificacion())) {
				clienteEncontrado = clienteService.obtenerPorIdentificacion(cuentaEntradaDto.getIdentificacion());
			} else {
				clienteEncontrado = clienteService.obtenerPorId(cuentaEntradaDto.getIdCliente());
			}

			if (null != clienteEncontrado && clienteEncontrado.isPresent()) {
				Cliente cliente = clienteEncontrado.get();
				if (EstadoEmun.INACTIVO.getDescripcion().equals(cliente.getEstado())) {
					return new ResponseEntity<>("El cliente se encuentra inactivo", HttpStatus.BAD_REQUEST);
				} else {
					Cuenta cuenta = new Cuenta();
					cuenta.setNumero(Integer.parseInt(cuentaEntradaDto.getNumero()));
					cuenta.setTipoCuenta(cuentaEntradaDto.getTipoCuenta());
					cuenta.setSaldoInicial(BigDecimal.valueOf(cuentaEntradaDto.getSaldoInicial()));
					cuenta.setEstado(EstadoEmun.ACTIVO.getDescripcion());
					cuenta.setIdCliente(clienteEncontrado.get().getClienteId());
					Cuenta cuentaGuardada = service.create(cuenta);
					return new ResponseEntity<Cuenta>(cuentaGuardada, HttpStatus.CREATED);
				}

			}
			return new ResponseEntity<>("No se encuentra registrado como cliente", HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * <b> Metodo para obtiene un cliente por su identidicacion. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param cuentaEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@GetMapping
	public ResponseEntity<?> obtenerCuentaPorCliente(@Validated @RequestBody CuentaEntradaDto cuentaEntradaDto) {
		try {
			Optional<Cliente> clienteEncontrado = null;
			if (!ObjectUtils.isEmpty(cuentaEntradaDto.getIdentificacion())) {
				clienteEncontrado = clienteService.obtenerPorIdentificacion(cuentaEntradaDto.getIdentificacion());
			} else {
				clienteEncontrado = clienteService.obtenerPorId(cuentaEntradaDto.getIdCliente());
			}
			if (ObjectUtils.isEmpty(clienteEncontrado)) {
				return new ResponseEntity<>("No existe el cliente", HttpStatus.BAD_REQUEST);
			}
			List<Cuenta> listaCuenta = service.obtenerPorCliente(clienteEncontrado.get().getClienteId());
			if (null == listaCuenta || listaCuenta.isEmpty()) {
				return new ResponseEntity<>("No existe cuenta con el id", HttpStatus.BAD_REQUEST);
			} else {
				return new ResponseEntity<List<Cuenta>>(listaCuenta, HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * 
	 * <b> Metodo que actualiza la cuenta. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param cuentaEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@PutMapping
	public ResponseEntity<?> update(@Validated @RequestBody CuentaEntradaDto cuentaEntradaDto) {
		try {
			if (ObjectUtils.isEmpty(cuentaEntradaDto.getIdCuenta())) {
				return new ResponseEntity<>("Debe colocar el id de la cuenta", HttpStatus.BAD_REQUEST);
			}
			Optional<Cuenta> cuenta = service.obtenerPorId(cuentaEntradaDto.getIdCuenta());
			if (cuenta.isPresent()) {
				Cuenta cuentaActualizar = cuenta.get();
				cuentaActualizar.setNumero(Integer.parseInt(cuentaEntradaDto.getNumero()));
				cuentaActualizar.setTipoCuenta(cuentaEntradaDto.getTipoCuenta());
				cuentaActualizar
						.setSaldoInicial(BigDecimal.valueOf(Double.valueOf(cuentaEntradaDto.getSaldoInicial())));
				cuentaActualizar.setEstado(cuentaEntradaDto.getEstado());
				Cuenta cuentaGuardada = service.update(cuentaActualizar);
				return new ResponseEntity<Cuenta>(cuentaGuardada, HttpStatus.OK);
			}
			return new ResponseEntity<>("No se encuentra la cuenta", HttpStatus.BAD_REQUEST);

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
			Optional<Cuenta> cuenta = service.obtenerPorId(id);
			if (cuenta.isPresent()) {
				service.delete(id);
				return new ResponseEntity<>("Registro Eliminado", HttpStatus.OK);
			}
			return new ResponseEntity<String>("No se puede eliminar la cuenta con id: " + id + " no existe el registro",
					HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
