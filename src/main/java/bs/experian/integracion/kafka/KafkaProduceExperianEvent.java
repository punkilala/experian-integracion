package bs.experian.integracion.kafka;

import org.apache.kafka.common.KafkaException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.integracion.infrastructure.config.IntegracionKafkaTopicsProperties;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import bs.experian.integracion.infrastructure.exceptions.AgoraException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceExperianEvent {
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final IntegracionKafkaTopicsProperties props;
	
	/**
	 * publicar en kafka eventos de Experian
	 * @param request
	 */
	public void publicar(ExperianWebhookEvent request) {

		try {
			String key = request.getQueryId();
            String value = objectMapper.writeValueAsString(request);
           
		  kafkaTemplate.send(props.getWebhookEvents(), key, value).get();
			
		} catch (JsonProcessingException e) {
	        log.error("Error serializando mensaje Kafka", e);
	        throw new AgoraException(
	            HttpStatus.INTERNAL_SERVER_ERROR.value(),
	            "ERR serializando evento Experian",
	            null);   
	    } catch (KafkaException | ExecutionException | InterruptedException e) {
    	    if (e instanceof InterruptedException) {
    	        Thread.currentThread().interrupt();
	    	}
	        log.error("Error interno del publicar en Kafka", e);
	        throw new AgoraException(
	            HttpStatus.INTERNAL_SERVER_ERROR.value(),
	            "ERR interno Kafka publicando evento Experian",
	            null);
	    }
		
	}

}
