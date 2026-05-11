package bs.experian.integracion.kafka.avro;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import bs.experian.events.avro.DocumentoDescargaResultAvro;
import bs.experian.integracion.domain.model.DescargaDocumentoModel;
import bs.experian.integracion.infrastructure.config.IntegracionKafkaTopicsProperties;
import bs.experian.integracion.infrastructure.exceptions.RetryableProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * publicar topic kafka resultado de la descarga del documento.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceDescargaDocumentosResultAvro {
	
	@Qualifier("avroKafkaTemplate")
	private final KafkaTemplate<String, Object> avroKafkaTemplate;
	
	public void publicar(DescargaDocumentoModel model) {
		
		String pdfEstado = model.getPdfDocument() != null ? "CUSTODIA_OK" : "NO_DESCARGADO";
		String jsonEstado= model.getJsonDocument() != null ? "DESCARGADO" : "NO_DESCARGADO";
		String resultDescarga =  pdfEstado.equals("CUSTODIA_OK") && jsonEstado.equals("DESCARGADO") ? "DESCARGA_COMPLETA" : "DESCARGA_INCOMPLETA";
		if (pdfEstado.equals("CUSTODIA_OK") && jsonEstado.equals("DESCARGADO")) {
			resultDescarga = "DESCARGA_COMPLETA" ;
		}
		
		if ((pdfEstado.equals("NO_DESCARGADO") && jsonEstado.equals("DESCARGADO")) || (pdfEstado.equals("NO_DESCARGADO") && jsonEstado.equals("DESCARGADO"))) {
			resultDescarga = "DESCARGA_PARCIAL" ;
		}
		
		if (pdfEstado.equals("CUSTODIA_OK") && jsonEstado.equals("NO_DESCARGADO")) {
			resultDescarga = "ERROR_DESCARGA" ;
		}
		try {
			
			DocumentoDescargaResultAvro evento = DocumentoDescargaResultAvro.newBuilder()
					.setQueryId(model.getQueryId())
					.setNotificationId(model.getNotificationId())
					.setEventType("ResultDescargaCustodiaDocumento")
					.setDocumentCode(model.getDocumentCode())
					.setResultadoDescarga(resultDescarga)
					.setJsonStatus(jsonEstado)
					.setJsonErrMsg(model.getErrorMensaje())
					.setPdfStatus(pdfEstado)
					.setPdfErrMsg(model.getErrorMensaje())
					.build();
			
			avroKafkaTemplate.send("documento.descarga.result.avro.v1", model.getQueryId(), evento).get(10, TimeUnit.SECONDS);
			
		} catch (KafkaException | ExecutionException | InterruptedException e) {
			if (e instanceof InterruptedException) {
    	        Thread.currentThread().interrupt();
	    	}
			log.error("Error al publicar en Kafka", e);
	        throw  new  RetryableProcessingException("Error al publicar en Kafka", e);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		 
	}
}
