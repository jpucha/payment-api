/**
 * 
 */
package ec.com.peigo.service.payment;

import java.util.List;
import java.util.Optional;

import ec.com.peigo.model.payment.Cuenta;

/**
 * 
 * <b> Interfaz del servicio para el Cuenta. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
public interface CuentaService {
	Cuenta create(Cuenta cuenta);

	List<Cuenta> read();

	Cuenta update(Cuenta cuenta);

	void delete(Long id);

	Optional<Cuenta> obtenerPorId(Long id);

	List<Cuenta> obtenerPorCliente(Long id);

	Optional<Cuenta> obtenerPorNumeroCuenta(int numeroCuenta);

}
