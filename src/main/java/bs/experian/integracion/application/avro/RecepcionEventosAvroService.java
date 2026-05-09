package bs.experian.integracion.application.avro;

import org.springframework.stereotype.Service;

import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import bs.experian.integracion.kafka.avro.KafkaProduceExperianEventAvro;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecepcionEventosAvroService {
	private final KafkaProduceExperianEventAvro kafkaProduceExperianEventAvro;
	
	public void publicarnEvento(ExperianWebhookEvent request) {
		kafkaProduceExperianEventAvro.publicar(request);
	}

}
