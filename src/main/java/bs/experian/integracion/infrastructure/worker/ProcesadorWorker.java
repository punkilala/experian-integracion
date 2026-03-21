package bs.experian.integracion.infrastructure.worker;

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
import static bs.experian.integracion.domain.constants.ExperianConstats.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcesadorWorker {

	
	private final ProcesarDocumentosRepository procesarDocumentosRepository;
	private final DescargarDocumentoClient descargarDocumentoClient;
	private final ObjectMapper objectMapper;
	private final ExperianEventosClient experianEventosClient;
	
	private static final int MAX_INTENTOS = 3;

	/**
	 * metodo principial de gestión de descargar
	 * @param documento
	 */
	public void procesar (ColaDescargaDocumentosEntity documento) {
		
		try {	
	        descargarContenidoSiEsNecesario(documento);
	
	        if (documento.getPdfDocumento() != null && documento.getJsonDocumento() != null) {
	        	notificarDescargaAOrquestador(documento);
	        }
	
	    } catch (Exception e) {
	        log.warn("ERR INTEGRACION-EXPERIAN Fallo descarga {} - {}: {}",  documento.getQueryId(), documento.getDocumentCode(), e.getMessage());
	        procesarDocumentosRepository.reprogramarDescarga(documento);
	        procesarDocumentosRepository.registrarErrorDescarga(documento, e);
	    }
	}
	
	/**
	 * Descargar documentos si proceder
	 * @param doc
	 */
	public void descargarContenidoSiEsNecesario (ColaDescargaDocumentosEntity doc) {
		if (doc.getPdfDocumento() == null) {
			try {
	            doc.setFase(PDF);
	            byte[] pdf = descargarDocumentoClient.descargar(doc.getPdfUrl(), byte[].class);
	            doc.setPdfDocumento(pdf);
	            procesarDocumentosRepository.guardarContenido(doc);

	        } catch (Exception e) {
	            doc.setErrorMensaje(e.getMessage());
	            procesarDocumentosRepository.registrarErrorDescarga(doc, e);
	        }
        }

        if (doc.getJsonDocumento() == null) {
        	String json = "";
        	try {
                doc.setFase(JSON);
                json = descargarDocumentoClient.descargar(doc.getJsonUrl(), String.class);
                String jsonBonito = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(json));
                doc.setJsonDocumento(jsonBonito);
                procesarDocumentosRepository.guardarContenido(doc);
        	} catch (JsonProcessingException e) {
        	    // JSON inválido pero descargado: guardamos el original
        	    doc.setJsonDocumento(json);
        	    procesarDocumentosRepository.guardarContenido(doc);
            } catch (Exception e) {
                procesarDocumentosRepository.registrarErrorDescarga(doc, e);
            }
        	if( null == doc.getPdfDocumento() || null == doc.getJsonDocumento()) {
        		gestionarIntentos(doc);
        	}
        }
	}
	
	/**
	 * politica de reintentos para no reintentar la descarga indefinidamente
	 * @param doc
	 */
	private void gestionarIntentos(ColaDescargaDocumentosEntity doc) {

        doc.setIntentos(doc.getIntentos()  +1);
        if (doc.getIntentos() >= MAX_INTENTOS) {
        	notificarDescargaAOrquestador(doc);
            return;
        }
        procesarDocumentosRepository.reprogramarDescarga(doc);
    }
	
	
	/**
	 * Reenvio al orquestador el resultado de la descarga;
	 * @param doc
	 */
	private void notificarDescargaAOrquestador (ColaDescargaDocumentosEntity doc) {
		
		boolean hayPdf = doc.getPdfDocumento() != null;
	    boolean hayJson = doc.getJsonDocumento() != null;

	    boolean resultadoDescarga = hayPdf && hayJson;
	    
		doc.setFase(REENVIO);
		
		//documento a custodiar
		if(hayPdf) {
			procesarDocumentosRepository.pdfToCustodia(doc);
		}
		
		String pdfStatus = hayPdf ? PTE_CUSTODIA : DOC_NO_DESCARGADO;
		String jsonPayload = hayJson ? doc.getJsonDocumento() : DOC_NO_DESCARGADO;
		String p="";
		DescargaDocumentoResponse descargaDocumentoResponse = DescargaDocumentoResponse.builder()
				.documentCode(doc.getDocumentCode())
				.status(STATUS_DOCUDMENTO_DESCARGADO)
				.substatus(resultadoDescarga ? DESCARGA_COMPLETA : DESCARGA_INCOMPLETA)
				.pdfDocument(pdfStatus)
				.jsonDocument(jsonPayload)
				.build();			
		
		JsonNode evenData = objectMapper.valueToTree(descargaDocumentoResponse);
		
		ExperianWebhookEvent event = new ExperianWebhookEvent();
		event.setQueryId(doc.getQueryId());
		event.setNotificationId(doc.getNotificationId());
		event.setOrigen("INTEGRACION");
		event.setEventType(TYPE_DOCUDMENTO_DESCARGADO);
		event.setEventData(evenData);
		
		//enviar aviso a orquestador
		experianEventosClient.reenviarEvento(event);
		//borrar de la cola de trabajo del worker
		procesarDocumentosRepository.borrarColaWorker(doc);

	}
	
	/**
	 * registrar error inesperado en la ejecucion del worker
	 * @param e
	 * @param origen
	 */
	public void gestionErrorWorker (Exception e) {
		ColaDescargaDocumentosEntity doc = new ColaDescargaDocumentosEntity();
		doc.setQueryId(UNDEFINED);
		doc.setDocumentCode(UNDEFINED);
		doc.setNotificationId(UNDEFINED);
		doc.setIntentos(0);
		doc.setFase(UNDEFINED);
		
		procesarDocumentosRepository.registrarErrorDescarga(doc, e);
	}
	
	

  


   

}
