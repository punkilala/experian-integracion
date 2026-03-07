package bs.experian.integracion.infrastructure.webclient;


import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import bs.experian.integracion.application.proveedor.ReenviadorEventoProveedor;
import bs.experian.integracion.infrastructure.config.IntegracionProperties;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import bs.experian.integracion.infrastructure.exceptions.WebclientErrorMapper;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class ExperianEventosClient implements ReenviadorEventoProveedor{
	
	private final WebClient webClient;
	private final IntegracionProperties props;
	
	@Override
	public void reenviarEvento(ExperianWebhookEvent request) {
		webClient.post()
			.uri(props.getOrquestador().getBaseUrl() + props.getOrquestador().getEventosUrl())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.retrieve()
			.onStatus(HttpStatusCode::isError,
				    resp -> WebclientErrorMapper.toAgoraException(resp, "Error en Orquestador-experian"))
			.toBodilessEntity()
			.block();
		
	}

}
