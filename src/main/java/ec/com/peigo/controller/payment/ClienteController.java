/**
 * 
 */
package ec.com.peigo.controller.payment;

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

import ec.com.peigo.controller.payment.dto.ClienteEntradaDto;
import ec.com.peigo.model.payment.Cliente;
import ec.com.peigo.service.payment.ClienteService;

/**
 * 
 * <b> Clase controlador de los clientes. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
	private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

	@Autowired
	private ClienteService service;

	/**
	 * 
	 * <b> Metodo que crea un cliente. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param clienteEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@PostMapping
	public ResponseEntity<?> create(@Validated @RequestBody ClienteEntradaDto clienteEntradaDto) {
		try {

			Optional<Cliente> clienteEncontrado = service
					.obtenerPorIdentificacion(clienteEntradaDto.getIdentificacion());
			if (clienteEncontrado.isPresent()) {
				return new ResponseEntity<>(
						"Cliente ya se encuentra registrado es estado: " + clienteEncontrado.get().getEstado(),
						HttpStatus.BAD_REQUEST);
			} else {
				Cliente cliente = new Cliente();
				cliente.setNombre(clienteEntradaDto.getNombre());
				cliente.setGenero(clienteEntradaDto.getGenero());
				cliente.setEdad(clienteEntradaDto.getEdad());
				cliente.setIdentificacion(clienteEntradaDto.getIdentificacion());
				cliente.setDireccion(clienteEntradaDto.getDireccion());
				cliente.setTelefono(clienteEntradaDto.getTelefono());
				cliente.setContrasena(clienteEntradaDto.getContrasena());
				cliente.setEstado(Boolean.TRUE.toString());
				Cliente clienteGuardado = service.create(cliente);
				return new ResponseEntity<Cliente>(clienteGuardado, HttpStatus.CREATED);
			}
		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * <b> Metodo para obtiene un cliente por su identidicacion. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param clienteEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@GetMapping
	public ResponseEntity<?> obtenerCliente(@Validated @RequestBody ClienteEntradaDto clienteEntradaDto) {
		try {
			Optional<Cliente> clienteEncontrado = service
					.obtenerPorIdentificacion(clienteEntradaDto.getIdentificacion());
			if (clienteEncontrado.isPresent()) {
				return new ResponseEntity<Cliente>(clienteEncontrado.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Cliente no existe.", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 * <b> Metodo que actualiza un cliente. </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param clienteEntradaDto
	 *            parametro de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@PutMapping
	public ResponseEntity<?> update(@Validated @RequestBody ClienteEntradaDto clienteEntradaDto) {
		try {
			Optional<Cliente> clienteEncontrado = null;
			if (!ObjectUtils.isEmpty(clienteEntradaDto.getIdentificacion())) {
				clienteEncontrado = service.obtenerPorIdentificacion(clienteEntradaDto.getIdentificacion());
			} else {
				clienteEncontrado = service.obtenerPorId(clienteEntradaDto.getIdCliente());
			}

			if (null != clienteEncontrado && clienteEncontrado.isPresent()) {
				Cliente cliente = clienteEncontrado.get();
				cliente.setNombre(clienteEntradaDto.getNombre());
				cliente.setGenero(clienteEntradaDto.getGenero());
				cliente.setEdad(clienteEntradaDto.getEdad());
				cliente.setIdentificacion(clienteEntradaDto.getIdentificacion());
				cliente.setDireccion(clienteEntradaDto.getDireccion());
				cliente.setTelefono(clienteEntradaDto.getTelefono());
				cliente.setContrasena(clienteEntradaDto.getContrasena());
				cliente.setEstado(clienteEntradaDto.getEstado());
				Cliente clienteGuardado = service.update(cliente);
				return new ResponseEntity<Cliente>(clienteGuardado, HttpStatus.OK);

			} else {
				return new ResponseEntity<>("Cliente no se encuentra registrado", HttpStatus.CREATED);
			}
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

			Optional<Cliente> personaEncontrada = service.obtenerPorId(id);
			if (personaEncontrada.isPresent()) {
				service.delete(id);
				return new ResponseEntity<>("Registro Eliminado", HttpStatus.OK);
			}
			return new ResponseEntity<String>(
					"No se puede eliminar el registro con id: " + id + " no existe el registro",
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
