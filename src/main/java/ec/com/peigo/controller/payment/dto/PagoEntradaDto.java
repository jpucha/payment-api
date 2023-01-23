package ec.com.peigo.controller.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoEntradaDto {

    private Integer cuentaOrigen;
    private Integer cuentaDestino;
    private BigDecimal saldo;

}
