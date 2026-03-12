package bs.experian.integracion.infrastructure.worker;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.integracion.infrastructure.dto.orquestador.DescargaDocumentoResponse;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import bs.experian.integracion.infrastructure.persistence.ProcesarDocumentosRepository;
import bs.experian.integracion.infrastructure.persistence.entity.ColaDescargaDocumentosEntity;
import bs.experian.integracion.infrastructure.webclient.DescargarDocumentoClient;
import bs.experian.integracion.infrastructure.webclient.ExperianEventosClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcesadorDescargaDocumento {

	
	private final ProcesarDocumentosRepository procesarDocumentosRepository;
	private final DescargarDocumentoClient descargarDocumentoClient;
	private final ObjectMapper objectMapper;
	private final ExperianEventosClient experianEventosClient;
	
	private static final int MAX_INTENTOS = 3;

	
	public void procesar (ColaDescargaDocumentosEntity documento) {
		
		try {	
	        descargarContenidoSiEsNecesario(documento);
	
	        if (documento.getPdfDocumento() != null && documento.getJsonDocumento() != null) {
	        	notificarDescargaAOrquestador(documento);
	        }
	
	    } catch (Exception e) {
	        log.warn("ERR INTEGRACION-EXPERIAN Fallo descarga {} - {}: {}",  documento.getQueryId(), documento.getDocumentCode(), e.getMessage());
	        documento.setErrorMensaje(e.getMessage());
	        gestionarIntentos(documento);
	        procesarDocumentosRepository.registrarErrorDescarga(documento, ProcesadorDescargaDocumento.class.getName());
	    }
	}
	
	public void descargarContenidoSiEsNecesario (ColaDescargaDocumentosEntity doc) {
		if (doc.getPdfDocumento() == null) {
			doc.setTipo("PDF");
            byte[] pdf = descargarDocumentoClient.descargar(doc.getPdfUrl(), byte[].class);
            doc.setPdfDocumento(pdf);
            procesarDocumentosRepository.guardarContenido(doc);
        }

        if (doc.getJsonDocumento() == null) {
        	doc.setTipo("JSON");
            String json = descargarDocumentoClient.descargar(doc.getJsonUrl(), String.class);
            try {
				String jsonBonito = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(json));
				doc.setJsonDocumento(jsonBonito);
			} catch (JsonProcessingException e) {
				doc.setJsonDocumento(json);
			} 
           
            procesarDocumentosRepository.guardarContenido(doc);
        }
	}
		
	private void gestionarIntentos(ColaDescargaDocumentosEntity doc) {

        doc.setIntentos(doc.getIntentos()  +1);
        if (doc.getIntentos() >= MAX_INTENTOS) {
        	notificarDescargaAOrquestador(doc);
            return;
        }
        
        OffsetDateTime siguiente = OffsetDateTime.now().plus(1, ChronoUnit.HOURS);
        doc.setSiguienteReintento(siguiente);

        procesarDocumentosRepository.reprogramarDescarga(doc);
    }
	
	private void notificarDescargaAOrquestador (ColaDescargaDocumentosEntity doc) {
		
		boolean resultadoDescarga =  doc.getPdfDocumento() != null && doc.getJsonDocumento() != null;
		
		DescargaDocumentoResponse descargaDocumentoResponse = DescargaDocumentoResponse.builder()
				.status("documento_descargado")
				.substatus(resultadoDescarga ? "OK" : "KO")
				.pdfDocument(doc.getPdfDocumento() != null)
				.jsonDocument(doc.getJsonDocumento())
				.build();			
		
		JsonNode evenData = objectMapper.valueToTree(descargaDocumentoResponse);
		
		ExperianWebhookEvent event = new ExperianWebhookEvent();
		event.setQueryId(doc.getQueryId());
		event.setNotificationId(UUID.randomUUID().toString());
		event.setEventType("DocumentoDescargado");
		event.setEventData(evenData);
		
		experianEventosClient.reenviarEvento(event);
		procesarDocumentosRepository.borrarColaWorker(doc);

	}

  


   

}
