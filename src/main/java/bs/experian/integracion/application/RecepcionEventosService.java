package bs.experian.integracion.application;

import org.springframework.stereotype.Service;

import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import bs.experian.integracion.kafka.KafkaProduceExperianEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecepcionEventosService {
	
	private final KafkaProduceExperianEvent kafkaProduceExperianEvent;
	
	public void ejecutar(ExperianWebhookEvent request) {
		kafkaProduceExperianEvent.publicar(request);
	}

}
