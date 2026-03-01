package bs.experian.integracion.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bs.experian.integracion.dto.crearSolicitud.ExperianResponse;
import bs.experian.integracion.dto.crearSolicitud.NuevaSolicitudRequest;
import bs.experian.integracion.dto.webhook.ExperianWebhookEvent;
import bs.experian.integracion.service.NuevaSolicitudService;
import bs.experian.integracion.service.ReenvioEventosService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/integracion")
@RequiredArgsConstructor
public class IntegracionControllers {
	private final NuevaSolicitudService nuevaSolicitudService;
	private final ReenvioEventosService reenvioEventosService;
	
	@PostMapping("/experian/solicitudes")
	public ResponseEntity<ExperianResponse> crearSolicitud (@RequestBody NuevaSolicitudRequest nuevaSolicitudRequest){
		return ResponseEntity.ok(nuevaSolicitudService.crearSolicitud(nuevaSolicitudRequest));	
	}
	
	@PostMapping("/experian/eventos")
	 public ResponseEntity<Void> webhook(@RequestBody ExperianWebhookEvent request) {

        if (request == null || request.getQueryId() == null || request.getQueryId().isBlank()
                || request.getEventType() == null || request.getEventType().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        reenvioEventosService.reenvioToOrquestador(request);
        
        return ResponseEntity.ok().build();
    }
	

}
