/**
 * 
 */
package ec.com.peigo.repository.payment;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ec.com.peigo.model.payment.Movimiento;

/**
 * 
 * <b> Interfaz del repositorio del movimiento. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

	List<Movimiento> findByIdClienteAndIdCuenta(Long idCliente, Long idCuenta);

	@Query(value = "SELECT c FROM Movimiento c WHERE c.cliente.identificacion= :identificacion AND c.cuenta.numero= :numero")
	List<Movimiento> buscarPorClienteCuenta(String identificacion, int numero);

	@Query(value = "SELECT COALESCE(SUM(c.valor),0) FROM Movimiento c WHERE c.cliente.clienteId= :clienteId AND c.cuenta.idCuenta= :idCuenta AND tipoMovimiento= :tipoMovimiento AND CONVERT(c.fechaRegistro, DATE) = CONVERT(:fecha, DATE)")
	Double sumaValorPorClienteCuentaFecha(Long clienteId, Long idCuenta, String tipoMovimiento, Date fecha);


}
