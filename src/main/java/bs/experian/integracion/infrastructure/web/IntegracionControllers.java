package bs.experian.integracion.infrastructure.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bs.experian.integracion.application.CrearSolicitudService;
import bs.experian.integracion.application.DescargarDocumentosService;
import bs.experian.integracion.application.RecepcionEventosService;
import bs.experian.integracion.infrastructure.dto.orquestador.DescargaDocumentoEvent;
import bs.experian.integracion.infrastructure.dto.orquestador.NuevaSolicitudRequest;
import bs.experian.integracion.infrastructure.dto.orquestador.NuevaSolicitudResponse;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/integracion")
@RequiredArgsConstructor
public class IntegracionControllers {
	private final CrearSolicitudService crearSolicitudService;
	private final RecepcionEventosService recepcionEventosService;
	private final DescargarDocumentosService descargarDocumentosService;
	
	@PostMapping("/experian/solicitudes")
	public ResponseEntity<NuevaSolicitudResponse> crearSolicitud (@RequestBody NuevaSolicitudRequest nuevaSolicitudRequest){
		return ResponseEntity.ok(crearSolicitudService.ejecutar(nuevaSolicitudRequest));	
	}
	
	@PostMapping("/experian/eventos")
	 public ResponseEntity<Void> webhook(@Valid @RequestBody ExperianWebhookEvent request) {
        recepcionEventosService.ejecutar(request);
        
        return ResponseEntity.ok().build();
    }
	
	@PostMapping("/experian/descarga-documento")
	public ResponseEntity<Void> descargarDocmento(@RequestBody DescargaDocumentoEvent request){
		descargarDocumentosService.descargarDocumento(request);
		return ResponseEntity.ok().build();
		
	}
	

}
