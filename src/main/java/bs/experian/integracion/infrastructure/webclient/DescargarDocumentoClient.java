package bs.experian.integracion.infrastructure.webclient;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DescargarDocumentoClient {
	
	private final WebClient webClient;
	
	public <T> T descargar(String url, Class<T> tipo) {
		return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(tipo)
                .block();

	}
	

}
