/**
 * 
 */
package ec.com.peigo.service.payment.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.com.peigo.model.payment.Cuenta;
import ec.com.peigo.repository.payment.CuentaRepository;
import ec.com.peigo.service.payment.CuentaService;

/**
 * 
 * <b> Servicio para la Cuenta. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@Service
public class CuentaServiceImpl implements CuentaService {

	@Autowired
	private CuentaRepository cuentaRepository;

	@Override
	public Cuenta create(Cuenta cuenta) {
		return cuentaRepository.save(cuenta);
	}

	@Override
	public List<Cuenta> read() {
		return cuentaRepository.findAll();
	}

	@Override
	public Cuenta update(Cuenta cuenta) {
		return cuentaRepository.save(cuenta);
	}

	@Override
	public void delete(Long id) {
		cuentaRepository.deleteById(id);

	}

	@Override
	public Optional<Cuenta> obtenerPorId(Long id) {
		return cuentaRepository.findById(id);
	}

	@Override
	public List<Cuenta> obtenerPorCliente(Long id) {
		return cuentaRepository.findByIdCliente(id);
	}

	@Override
	public Optional<Cuenta> obtenerPorNumeroCuenta(int numeroCuenta) {
		return cuentaRepository.findByNumero(numeroCuenta);
	}

}
