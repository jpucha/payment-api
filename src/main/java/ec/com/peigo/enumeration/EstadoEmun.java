/**
 * 
 */
package ec.com.peigo.enumeration;

import lombok.Getter;

/**
 * 
 * <b> Enumeracion de los estados. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@Getter
public enum EstadoEmun {

	ACTIVO("A", "True"), INACTIVO("I", "False");

	private String codigo;
	private String descripcion;

	private EstadoEmun(String codigo, String descripcion) {
		this.codigo = codigo;
		this.descripcion = descripcion;
	}
}
