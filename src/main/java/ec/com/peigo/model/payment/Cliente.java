package ec.com.peigo.model.payment;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * <b> Clase entidad de la tabla cliente. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@Getter
@Setter
@DynamicUpdate
@Entity
@NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c")
public class Cliente extends Persona implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cliente_Id")
	private Long clienteId;

	private String contrasena;

	private String estado;

	@OneToMany(mappedBy = "cliente")
	@JsonIgnore
	private List<Cuenta> cuentas;

	@OneToMany(mappedBy = "cliente")
	@JsonIgnore
	private List<Movimiento> movimientos;
}