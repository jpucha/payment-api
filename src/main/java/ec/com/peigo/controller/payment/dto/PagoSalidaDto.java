package ec.com.peigo.controller.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagoSalidaDto {

    private Long numeroOperacion;

    private BigDecimal saldoCuentaOrigen;

    private BigDecimal saldoCuentaDestino;


}
