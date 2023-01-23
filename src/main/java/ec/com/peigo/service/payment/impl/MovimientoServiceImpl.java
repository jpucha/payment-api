/**
 * 
 */
package ec.com.peigo.service.payment.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.com.peigo.controller.payment.dto.ReporteDto;
import ec.com.peigo.model.payment.Movimiento;
import ec.com.peigo.repository.payment.MovimientoRepository;
import ec.com.peigo.service.payment.MovimientoService;

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

	@Autowired
	private MovimientoRepository movimientoRepository;

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

	@Override
	public List<ReporteDto> obtenerPorFechas(Date fechaInicial, Date fechaFinal) {
		return movimientoRepository.buscarPorEntreFechas(fechaInicial, fechaFinal);
	}

}
