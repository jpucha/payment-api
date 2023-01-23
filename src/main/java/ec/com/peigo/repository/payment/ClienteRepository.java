/**
 * 
 */
package ec.com.peigo.repository.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.com.peigo.model.payment.Cliente;

/**
 * 
 * <b> Interfaz del repositorio del cliente. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	Optional<Cliente> findByEstadoAndClienteId(String estado, Long id);

	Optional<Cliente> findByClienteId(Long id);

	Optional<Cliente> findByIdentificacion(String id);

}
