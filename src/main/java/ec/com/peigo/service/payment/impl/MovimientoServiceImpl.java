/**
 * 
 */
package ec.com.peigo.service.payment.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import ec.com.peigo.controller.payment.dto.PaymentRequest;
import ec.com.peigo.controller.payment.dto.PaymentResponse;
import ec.com.peigo.controller.payment.dto.ResponseAccountClient;
import ec.com.peigo.enumeration.TransactionTypeEnum;
import ec.com.peigo.model.payment.Cliente;
import ec.com.peigo.model.payment.Cuenta;
import ec.com.peigo.service.payment.CuentaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ec.com.peigo.model.payment.Movimiento;
import ec.com.peigo.repository.payment.MovimientoRepository;
import ec.com.peigo.service.payment.MovimientoService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * <b> Servicio para el Movimiento. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@Service
public class MovimientoServiceImpl implements MovimientoService {

	private static final Logger log = LoggerFactory.getLogger(MovimientoServiceImpl.class);

	public static final String USER_MOD = "peigoadmin";

	@Autowired
	private MovimientoRepository movimientoRepository;

	@Autowired
	private CuentaService cuentaService;

	@Override
	public Movimiento create(Movimiento movimiento) {

		return movimientoRepository.save(movimiento);
	}

	@Override
	public List<Movimiento> read() {

		return movimientoRepository.findAll();
	}

	@Override
	public Movimiento update(Movimiento movimiento) {
		return movimientoRepository.save(movimiento);
	}

	@Override
	public void delete(Long id) {
		movimientoRepository.deleteById(id);

	}

	@Override
	public Optional<Movimiento> obtenerPorId(Long id) {

		return movimientoRepository.findById(id);
	}

	@Override
	public List<Movimiento> obtenerPorClienteCuenta(Long idCliente, Long idCuenta) {
		return movimientoRepository.findByIdClienteAndIdCuenta(idCliente, idCuenta);
	}

	@Override
	public List<Movimiento> obtenerPorIdentificacionNumeroCuenta(String identificacion, int numeroCuenta) {
		return movimientoRepository.buscarPorClienteCuenta(identificacion, numeroCuenta);
	}

	@Override
	public Double obtenerSumaValorClienteCuentaFecha(Long clienteId, Long idCuenta, String tipoMovimiento, Date fecha) {
		return movimientoRepository.sumaValorPorClienteCuentaFecha(clienteId, idCuenta, tipoMovimiento, fecha);
	}

	@Transactional
	@Override
	public ResponseEntity<?> createPaymentTransaction(ResponseAccountClient responseDto, PaymentRequest paymentInDto, String transactionNumber ) throws Exception {
		try {
			//Create debit transaction
			Movimiento debit = createTransaction(responseDto.getCuentaOrigen(), responseDto.getClienteOrigen(),
					paymentInDto.getMonto(), TransactionTypeEnum.DEBITO.getDescripcion(), transactionNumber);
			//Update origin account
			responseDto.getCuentaOrigen().get().setSaldoInicial(debit.getSaldo());
			cuentaService.update(responseDto.getCuentaOrigen().get());
			//Create credit transaction
			Movimiento credit = createTransaction(responseDto.getCuentaDestino(), responseDto.getClienteDestino(),
					paymentInDto.getMonto(), TransactionTypeEnum.CREDITO.getDescripcion(), transactionNumber);
			//Update destination account
			responseDto.getCuentaDestino().get().setSaldoInicial(credit.getSaldo());
			cuentaService.update(responseDto.getCuentaDestino().get());
			//result
			PaymentResponse result = new PaymentResponse();
			result.setNumeroOperacion(transactionNumber);
			result.setSaldoCuentaOrigen(debit.getSaldo());
			result.setSaldoCuentaDestino(credit.getSaldo());
			return new ResponseEntity<PaymentResponse>(result, HttpStatus.CREATED);
		}catch (Exception e){
			log.error("Error: createPaymentTransaction: ", e);
			throw new Exception(e);
		}

	}

	/**
	 * Create debit transaction.
	 * @param cuenta, Account.
	 * @param cliente, Client.
	 * @param valor, Value to subtract from the origen client balance.
	 * @param tipoMovimiento, Transaction type.
	 * @return movimiento object.
	 */
	private Movimiento createTransaction (Optional<Cuenta> cuenta, Optional<Cliente> cliente, BigDecimal valor,
										  String tipoMovimiento, String transactionNumber) throws Exception {
		Movimiento transaction = new Movimiento();
		try{

			transaction.setIdCliente(cuenta.get().getIdCliente());
			transaction.setIdCuenta(cuenta.get().getIdCuenta());
			transaction.setNumeroTransaccion(transactionNumber);
			transaction.setValor(valor);
			transaction.setTipoMovimiento(tipoMovimiento);
			transaction.setSaldoAnterior(cuenta.get().getSaldoInicial());
			if(StringUtils.equalsAnyIgnoreCase(TransactionTypeEnum.DEBITO.getDescripcion(),tipoMovimiento) ){
				transaction.setSaldo(transaction.getSaldoAnterior().subtract(transaction.getValor()));
			}else{
				transaction.setSaldo(transaction.getSaldoAnterior().add(transaction.getValor()));
			}
			transaction.setFechaRegistro(new Date());
			transaction.setUsuarioRegistro(USER_MOD);
			//save debito
			transaction = create(transaction);
			return transaction;
		}catch (Exception e){
			log.error("Error: createTransaction: " + tipoMovimiento, e);
			throw new Exception(e);
		}
	}
}
