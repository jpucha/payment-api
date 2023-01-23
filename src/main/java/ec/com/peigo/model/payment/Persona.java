package ec.com.peigo.model.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * 
 * <b> Clase persona que sera heradad por cliente. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@Getter
@Setter
@MappedSuperclass
public class Persona implements Serializable {
	private static final long serialVersionUID = 1L;

	private String direccion;

	private int edad;

	private String genero;

	private String identificacion;

	private String nombre;

	private int telefono;
}