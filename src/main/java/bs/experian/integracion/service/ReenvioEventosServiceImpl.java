package bs.experian.integracion.service;


import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import bs.experian.integracion.config.IntegracionProperties;
import bs.experian.integracion.dto.webhook.ExperianWebhookEvent;
import bs.experian.integracion.exceptions.WebclientErrorMapper;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class ReenvioEventosServiceImpl implements ReenvioEventosService {
	
	private final WebClient webClient;
	private final IntegracionProperties props;
	
	@Override
	public void reenvioToOrquestador(ExperianWebhookEvent request) {
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
