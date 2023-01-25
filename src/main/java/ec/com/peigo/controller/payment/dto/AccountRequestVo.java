/**
 * 
 */
package ec.com.peigo.controller.payment.dto;

import lombok.Data;

/**
 * 
 * <b> Clase Dto para la entrada de la cuenta. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@Data
public class AccountRequestVo {

	private Long idCuenta;
	private String identificacion;
	private String numero;
	private String tipoCuenta;
	private double saldoInicial;
	private String estado;
	private Long idCliente;
	private String numeroCuenta;
}
