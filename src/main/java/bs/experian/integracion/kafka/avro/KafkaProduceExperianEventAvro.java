package bs.experian.integracion.kafka.avro;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import bs.experian.integracion.infrastructure.exceptions.AgoraException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import bs.experian.events.avro.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceExperianEventAvro {
	@Qualifier("avroKafkaTemplate")
	private final KafkaTemplate<String, Object> avroKafkaTemplate;
	private final ObjectMapper objectMapper;

	
	/**
	 * publicar en kafka eventos de Experian con avro
	 * @param request
	 */
	public void publicar(ExperianWebhookEvent request) {

		try {
			String payloadExperianJson =objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
			Object avroEvent = construirEventoAvro(request, payloadExperianJson);
		    String topic = resolverTopic(request.getEventType());
			String key = request.getQueryId();
           
			avroKafkaTemplate.send(topic, key, avroEvent).get(10, TimeUnit.SECONDS);
			  
		} catch (IllegalArgumentException e) {

		    log.warn("Evento Experian no soportado: {}", request.getEventType());

		    throw new AgoraException(
		        HttpStatus.BAD_REQUEST.value(),
		        "Evento Experian no soportado: " + request.getEventType(),
		        null
		    );
		} catch (KafkaException | ExecutionException | InterruptedException e) {
    	    if (e instanceof InterruptedException) {
    	        Thread.currentThread().interrupt();
	    	}
	        log.error("Error interno del publicar en Kafka", e);
	        throw new AgoraException(
	            HttpStatus.INTERNAL_SERVER_ERROR.value(),
	            "ERR interno Kafka publicando evento Experian",
	            null);
	    } catch (TimeoutException e) {
	    	throw new AgoraException(
		            HttpStatus.INTERNAL_SERVER_ERROR.value(),
		            "ERR timeout Kafka publicando evento Experian",
		            null);
		} catch (JsonProcessingException e) {
			throw new AgoraException(
		            HttpStatus.INTERNAL_SERVER_ERROR.value(),
		            "ERR timeout Kafka publicando evento Experian",
		            null);
		}
		
	}
	
	private Object construirEventoAvro (ExperianWebhookEvent request, String payloadExperianJson) {
		JsonNode eventData = request.getEventData();
		return switch (request.getEventType()) {
			case "StatusChanged" ->  ExperianStatusChangedEventAvro.newBuilder()
		        .setQueryId(request.getQueryId())
		        .setNotificationId(request.getNotificationId())
		        .setEventType(request.getEventType())
		        .setStatus(eventData.path("status").asText())
		        .setSubstatus(eventData.path("substatus").asText(null))
		        .setPayloadExperianJson(payloadExperianJson)
		        .build();
			
			
			case "NewDocumentAvailable" -> ExperianNewDocumentAvailableEventAvro.newBuilder()
		        .setQueryId(request.getQueryId())
		        .setNotificationId(request.getNotificationId())
		        .setEventType(request.getEventType())
		        .setDocumentCode(eventData.path("documentCode").asText())
		        .setPdfDocumentUrl(eventData.path("pdfDocumentUrl").asText())
		        .setJsonDocumentUrl(eventData.path("jsonDocumentUrl").asText())
		        .setPayloadExperianJson(payloadExperianJson)
		        .build();
			
			default ->
				throw new IllegalArgumentException("Evento de Experian no reconocido: " + request.getEventType());
			};
		
	}
	
	private String resolverTopic(String eventType) {
        return switch (eventType) {
            case "StatusChanged" -> "experian.statusChanged.avro.v3";
            case "NewDocumentAvailable" -> "experian.newDocumentAvailable.avro.v3";
            default -> throw new IllegalArgumentException(
                    "Topic no configurado para eventType: " + eventType);
        };
    }

}
