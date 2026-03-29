package bs.experian.integracion.kafka;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.integracion.domain.model.DescargaDocumentoModel;
import bs.experian.integracion.infrastructure.config.IntegracionKafkaTopicsProperties;
import bs.experian.integracion.infrastructure.dto.orquestador.DescargaDocumentoEvent;
import bs.experian.integracion.infrastructure.dto.orquestador.DescargaDocumentoEventData;
import bs.experian.integracion.infrastructure.exceptions.NonRetryableProcessingException;
import bs.experian.integracion.infrastructure.exceptions.RetryableProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * publicar topic kafka resultado de la descarga del documento.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceDescargaDocumentosResult {
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final IntegracionKafkaTopicsProperties props;
	
	public void publicar(DescargaDocumentoModel model) {
		
		String pdfEstado = model.getPdfDocument() != null ? "PTE_CUSTODIA" : "NO_DESCARGADO";
		String jsonEstado= model.getJsonDocument() != null ? "DESCARGADO" : "NO_DESCARGADO";
		String resultDescarga = pdfEstado.equals("PTE_CUSTODIA") && jsonEstado.equals("DESCARGADO") ? "DESCARGA_COMPLETA" : "DESCARGA_INCOMPLETA";

		try {
			DescargaDocumentoEvent event = DescargaDocumentoEvent.builder()
					.queryId(model.getQueryId())
					.notificationId(model.getNotificationId())
					.eventType("DocumentoDescargado")
					.eventData(
							DescargaDocumentoEventData.builder()
								.status("documento_descargado")
								.substatus(resultDescarga)
								.documentCode(model.getDocumentCode())
								.pdfEstado(pdfEstado)
								.jsonEstado(jsonEstado)
								.build()
							)
					.build();
			
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send(props.getDescargaResultado(), model.getQueryId(), payload).get();
			
		} catch (JsonProcessingException e) {
			  log.error("Error serializando mensaje Kafka", e);
		      throw  new  NonRetryableProcessingException("Error serializando mensaje Kafka", e);
		}catch (KafkaException | ExecutionException | InterruptedException e) {
			if (e instanceof InterruptedException) {
    	        Thread.currentThread().interrupt();
	    	}
			log.error("Error al publicar en Kafka", e);
	        throw  new  RetryableProcessingException("Error al publicar en Kafka", e);
		}
		 
	}
}
