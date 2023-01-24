package ec.com.peigo.controller.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentOutDto {

    private String numeroOperacion;

    private BigDecimal saldoCuentaOrigen;

    private BigDecimal saldoCuentaDestino;


}
