/**
 * 
 */
package ec.com.peigo.repository.payment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.com.peigo.model.payment.Cuenta;

/**
 * 
 * <b> Interfaz del repositorio del Cuenta. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

	List<Cuenta> findByIdCliente(Long id);

	Optional<Cuenta> findByNumero(int numero);
}
