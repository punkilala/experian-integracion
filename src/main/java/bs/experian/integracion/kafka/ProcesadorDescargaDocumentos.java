package bs.experian.integracion.kafka;


import static bs.experian.integracion.domain.constants.ExperianConstats.JSON;
import static bs.experian.integracion.domain.constants.ExperianConstats.PDF;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.integracion.domain.model.DescargaDocumentoModel;
import bs.experian.integracion.infrastructure.exceptions.RetryableProcessingException;
import bs.experian.integracion.infrastructure.persistence.ProcesarDocumentosRepository;
import bs.experian.integracion.infrastructure.webclient.DescargarDocumentoClient;
import bs.experian.integracion.kafka.avro.KafkaProduceDescargaDocumentosResultAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcesadorDescargaDocumentos {

	
	private final ProcesarDocumentosRepository procesarDocumentosRepository;
	private final DescargarDocumentoClient descargarDocumentoClient;
	private final ObjectMapper objectMapper;
	private final KafkaProduceDescargaDocumentosResultAvro kafkaProduceDescargaDocumentosResultAvro;


	/**
	 * metodo principial de gestión de descargar
	 * @param documento
	 */
	public void procesar (DescargaDocumentoModel documento) {
		
		procesarDocumentosRepository.recuperarDoocumentosPteDescarga(documento);
		
		//termino politica de reintentos de descarga
		if(documento.getIntentos() >= 4) {
			kafkaProduceDescargaDocumentosResultAvro.publicar(documento);
			return;
		}
        descargarContenidoSiEsNecesario(documento);

        if (documento.getPdfDocument() != null && documento.getJsonDocument() != null) {
        	//documentos descargados correctamente
        	kafkaProduceDescargaDocumentosResultAvro.publicar(documento);
        }else {
        	throw new RetryableProcessingException("no se han descargado todos los documentos", null);
        }

	}
	
	/**
	 * Descargar documentos si proceder
	 * @param doc
	 */
	public void descargarContenidoSiEsNecesario (DescargaDocumentoModel doc) {
		if (doc.getPdfDocument() == null) {
			try {
	            doc.setTipoDocumento(PDF);
	            byte[] pdf = descargarDocumentoClient.descargar(doc.getPdfUrl(), byte[].class);
	            doc.setPdfDocument(pdf);

	        } catch (Exception e) {
	            doc.setErrorMensaje(e.getMessage());
	            procesarDocumentosRepository.registrarErrorDescarga(doc, e);
	        }
        }

        if (doc.getJsonDocument() == null) {
        	String json = "";
        	try {
                doc.setTipoDocumento(JSON);
                json = descargarDocumentoClient.descargar(doc.getJsonUrl(), String.class);
                String jsonBonito = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readTree(json));
                doc.setJsonDocument(jsonBonito);
        	} catch (JsonProcessingException e) {
        	    // JSON inválido pero descargado: guardamos el original
        	    doc.setJsonDocument(json);
            } catch (Exception e) {
                procesarDocumentosRepository.registrarErrorDescarga(doc, e);
            }	
        }
        
        procesarDocumentosRepository.guardarDocumentosEnTemporal(doc);
	}

}
