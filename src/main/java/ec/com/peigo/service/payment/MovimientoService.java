/**
 * 
 */
package ec.com.peigo.service.payment;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import ec.com.peigo.controller.payment.dto.PaymentRequest;
import ec.com.peigo.controller.payment.dto.ResponseAccountClient;
import ec.com.peigo.model.payment.Movimiento;
import org.springframework.http.ResponseEntity;

/**
 * 
 * <b> Interfaz del servicio para el Movimiento. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
public interface MovimientoService {
	Movimiento create(Movimiento movimiento);

	List<Movimiento> read();

	Movimiento update(Movimiento movimiento);

	void delete(Long id);

	Optional<Movimiento> obtenerPorId(Long id);

	List<Movimiento> obtenerPorClienteCuenta(Long idCliente, Long idCuenta);

	List<Movimiento> obtenerPorIdentificacionNumeroCuenta(String identificacion, int numeroCuenta);

	Double obtenerSumaValorClienteCuentaFecha(Long clienteId, Long idCuenta, String tipoMovimiento, Date fecha);

	/**
	 * @autor jpucha
	 * Create payment transaction.
	 * */
	ResponseEntity<?> createPaymentTransaction(ResponseAccountClient responseDto, PaymentRequest paymentInDto, String transactionNumber) throws Exception;

}
