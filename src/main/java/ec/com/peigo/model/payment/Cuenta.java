package ec.com.peigo.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * <b> Clase entidad de la tabla cuenta. </b>
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
@NamedQuery(name = "Cuenta.findAll", query = "SELECT c FROM Cuenta c")
public class Cuenta implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cuenta")
	private Long idCuenta;

	@NotNull
	private String estado;

	@Column(unique = true)
	@NotNull
	private int numero;

	@Column(name = "saldo_inicial")
	@NotNull
	private BigDecimal saldoInicial;

	@Column(name = "tipo_cuenta")
	private String tipoCuenta;

	// bi-directional many-to-one association to Cliente
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente", insertable = false, updatable = false)
	@JsonIgnore
	private Cliente cliente;

	// bi-directional many-to-one association to Movimiento
	@OneToMany(mappedBy = "cuenta")
	@JsonIgnore
	private List<Movimiento> movimientos;

	@NotNull
	@Column(name = "id_cliente")
	private Long idCliente;
}