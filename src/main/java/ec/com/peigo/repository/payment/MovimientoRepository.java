/**
 * 
 */
package ec.com.peigo.repository.payment;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ec.com.peigo.controller.payment.dto.ReporteDto;
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

	@Query(value = "SELECT COALESCE(SUM(c.valor),0) FROM Movimiento c WHERE c.cliente.clienteId= :clienteId AND c.cuenta.idCuenta= :idCuenta AND tipoMovimiento= :tipoMovimiento AND CONVERT(c.fecha, DATE) = CONVERT(:fecha, DATE)")
	Double sumaValorPorClienteCuentaFecha(Long clienteId, Long idCuenta, String tipoMovimiento, Date fecha);

	@Query(value = "SELECT CONVERT(m.fecha, DATE) as fecha, cl.nombre, cu.numero, cu.tipo_cuenta as tipoCuenta,  m.saldo_anterior as saldoAnterior, cu.estado, m.valor, m.saldo "
			+ "FROM cuentabancaria.movimiento m, cuentabancaria.cuenta cu, cuentabancaria.cliente cl "
			+ "where m.id_cliente = cl.cliente_Id and m.id_cuenta = cu.id_cuenta and cu.id_cliente = cl.cliente_Id "
			+ "and CONVERT(m.fecha, DATE) between CONVERT(:fechaInicial, DATE) AND CONVERT(:fechaFinal, DATE)", nativeQuery = true)
	List<ReporteDto> buscarPorEntreFechas(Date fechaInicial, Date fechaFinal);

}
