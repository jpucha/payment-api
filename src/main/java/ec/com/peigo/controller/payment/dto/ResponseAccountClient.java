package ec.com.peigo.controller.payment.dto;

import ec.com.peigo.model.payment.Cliente;
import ec.com.peigo.model.payment.Cuenta;
import lombok.Data;

import java.util.Optional;

/**
 * @author jpucha
 * Response manager class.
 */
@Data
public class ResponseAccountClient {

    private String code;

    private String errorMessage;

    private Optional<Cuenta> cuenta;

    private Optional<Cliente> cliente;

    private Optional<Cuenta> cuentaOrigen;

    private Optional<Cliente> clienteOrigen;

    private Optional<Cuenta> cuentaDestino;

    private Optional<Cliente> clienteDestino;

}