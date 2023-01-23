/**
 * 
 */
package ec.com.peigo.controller.payment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.com.peigo.controller.payment.dto.ReporteDto;
import ec.com.peigo.service.payment.MovimientoService;

/**
 * 
 * <b> Clase controlador de para los reportes. </b>
 * 
 * @author jpucha
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: jpucha $, $Date: 22 ene. 2023 $]
 *          </p>
 */
@RestController
@RequestMapping("/api")
public class GenerarReporteController {
	private static final Logger log = LoggerFactory.getLogger(GenerarReporteController.class);

	@Autowired
	private MovimientoService service;

	/**
	 * 
	 * <b> Metodo que genera el reporte por fechas, la fecha esta separada por
	 * coma(,). </b>
	 * <p>
	 * [Author: jpucha, Date: 22 ene. 2023]
	 * </p>
	 *
	 * @param fecha
	 *            fechas de entrada
	 * @return ResponseEntity<?> lista o mensaje de error
	 */
	@GetMapping(value = "/reportes")
	public ResponseEntity<?> reporte(@RequestParam String fecha) {

		try {
			if (ObjectUtils.isEmpty(fecha)) {
				return new ResponseEntity<>("Fechas Obligatorias", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			String[] fechas = fecha.split(",");
			if (fechas.length != 2) {
				return new ResponseEntity<>("Coloque la dos fechas", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date fechaInicial = formatter.parse(fechas[0]);
			Date fechaFinal = formatter.parse(fechas[1]);
			List<ReporteDto> lista = service.obtenerPorFechas(fechaInicial, fechaFinal);
			if (ObjectUtils.isEmpty(lista)) {
				return new ResponseEntity<>("No se encuentra datos con los parametros indicados",
						HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<List<ReporteDto>>(lista, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Por favor comuniquese con el administrador", e);
			return new ResponseEntity<>("Por favor comuniquese con el administrador", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
