package ec.com.peigo.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * <b> Clase entidad de la tabla movimiento. </b>
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
@NamedQuery(name = "Movimiento.findAll", query = "SELECT m FROM Movimiento m")
public class Movimiento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idMovimiento;

	@Column(name = "numero_transaccion")
	private String numeroTransaccion;

	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;

	private BigDecimal saldo;

	@Column(name = "tipo_movimiento")
	private String tipoMovimiento;

	private BigDecimal valor;

	@Column(name = "saldo_anterior")
	private BigDecimal saldoAnterior;

	// bi-directional many-to-one association to Cliente
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idCliente", insertable = false, updatable = false)
	@JsonIgnore
	private Cliente cliente;

	@Column
	private Long idCliente;

	// bi-directional many-to-one association to Cuenta
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cuenta", insertable = false, updatable = false)
	@JsonIgnore
	private Cuenta cuenta;

	@Column(name = "id_cuenta")
	private Long idCuenta;
}