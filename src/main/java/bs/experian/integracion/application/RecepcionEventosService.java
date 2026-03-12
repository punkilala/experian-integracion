package bs.experian.integracion.application;

import org.springframework.stereotype.Service;

import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import bs.experian.integracion.infrastructure.webclient.ExperianEventosClient;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecepcionEventosService {
	
	private final ExperianEventosClient experianEventosClient;
	
	public void ejecutar(ExperianWebhookEvent request) {
		experianEventosClient.reenviarEvento(request);
	}

}
