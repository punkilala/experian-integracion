package bs.experian.integracion.infrastructure.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bs.experian.integracion.application.casosuso.CrearSolicitudCasoUso;
import bs.experian.integracion.application.casosuso.ReenviarEventosCasoUso;
import bs.experian.integracion.infrastructure.dto.orquestador.DescargaDocumentoRequest;
import bs.experian.integracion.infrastructure.dto.orquestador.NuevaSolicitudRequest;
import bs.experian.integracion.infrastructure.dto.orquestador.NuevaSolicitudResponse;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/integracion")
@RequiredArgsConstructor
public class IntegracionControllers {
	private final CrearSolicitudCasoUso crearSolicitudCasoUso;
	private final ReenviarEventosCasoUso reenviarEventosCasoUso;
	
	@PostMapping("/experian/solicitudes")
	public ResponseEntity<NuevaSolicitudResponse> crearSolicitud (@RequestBody NuevaSolicitudRequest nuevaSolicitudRequest){
		return ResponseEntity.ok(crearSolicitudCasoUso.ejecutar(nuevaSolicitudRequest));	
	}
	
	@PostMapping("/experian/eventos")
	 public ResponseEntity<Void> webhook(@RequestBody ExperianWebhookEvent request) {

        if (request == null || request.getQueryId() == null || request.getQueryId().isBlank()
                || request.getEventType() == null || request.getEventType().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        reenviarEventosCasoUso.ejecutar(request);
        
        return ResponseEntity.ok().build();
    }
	
	@PostMapping("/experian/descarga-documento")
	public ResponseEntity<Void> descargarDocmento(@RequestBody DescargaDocumentoRequest request){
		
		
		return ResponseEntity.ok().build();
		
	}
	

}
