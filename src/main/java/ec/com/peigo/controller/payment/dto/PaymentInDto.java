package ec.com.peigo.controller.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentInDto {

    private Integer cuentaOrigen;

    private Integer cuentaDestino;

    private BigDecimal monto;
}
