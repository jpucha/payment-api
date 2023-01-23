/**
 * 
 */
package ec.com.peigo.service.payment;

import java.util.List;
import java.util.Optional;

import ec.com.peigo.model.payment.Cliente;

/**
 * 
 * <b> Interfaz del servicio para el cliente. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
public interface ClienteService {
	Cliente create(Cliente cliente);

	List<Cliente> read();

	Cliente update(Cliente cliente);

	void delete(Long id);

	Optional<Cliente> obtenerPorEstadoIdCliente(String estado, Long id);

	Optional<Cliente> obtenerPorId(Long id);

	Optional<Cliente> obtenerPorIdCliente(Long id);

	// List<Cliente> obtenerPorIdPersona(Long id);

	Optional<Cliente> obtenerPorIdentificacion(String identificacion);

}
