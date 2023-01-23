package ec.com.peigo.controller.payment.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * <b> Interfaz para obtener los datos del reporte entre fechas. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
public interface ReporteDto {

	public Date getFecha();

	public String getNombre();

	public int getNumero();

	public String getTipoCuenta();

	public BigDecimal getSaldoAnterior();

	public String getEstado();

	public BigDecimal getValor();

	public BigDecimal getSaldo();

}
