package bs.experian.integracion.kafka;

import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.RetryTopicHeaders;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bs.experian.integracion.domain.model.DescargaDocumentoModel;
import bs.experian.integracion.infrastructure.exceptions.NonRetryableProcessingException;
import bs.experian.integracion.infrastructure.exceptions.RetryableProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumeDescargaDocumentos {
	
	private final ObjectMapper objectMapper;
	private final ProcesadorDescargaDocumentos procesadorDescargaDocumentos;
	
	@RetryableTopic(
            attempts = "100", 
            backoff = @Backoff(delay = 1000),
            exclude = {NonRetryableProcessingException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(
            topics =  "${app.kafka.topics.descarga-orden}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listener(String mensaje,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String queryId) throws JsonProcessingException{
		
		try {
			DescargaDocumentoModel documento = objectMapper.readValue(mensaje, DescargaDocumentoModel.class);
			procesadorDescargaDocumentos.procesar(documento);

		} catch ( KafkaException e) {
	        log.error("Error Kafka procesando evento Experian", e);
	        throw new RetryableProcessingException("Error Kafka procesando evento Experian", e);
	    }catch (RetryableProcessingException | NonRetryableProcessingException e) {
	        throw e;
	    } catch (Exception e) {
	        log.error("Error inesperado procesando evento Experian", e);
	        throw new RetryableProcessingException("Error inesperado procesando evento Experian", e);
	    }
    }
	
	@DltHandler
    public void dlt(
    		String mensaje,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String queryId,
            @Header(name = "kafka_exception-message", required = false) byte[] exceptionMessage,
            @Header(name = "kafka_exception-stacktrace", required = false) byte[] exceptionStacktrace) {
    	
		log.error("Error inesperado procesando evento Experian");

    }
    		
}
