package bs.experian.integracion.infrastructure.webclient;



import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import bs.experian.integracion.application.proveedor.CreadorSolicitudProveedor;
import bs.experian.integracion.infrastructure.config.IntegracionProperties;
import bs.experian.integracion.infrastructure.dto.orquestador.NuevaSolicitudResponse;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianSolicitudPFRequest;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianSolicitudRequest;
import bs.experian.integracion.infrastructure.exceptions.WebclientErrorMapper;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ExperianSolicitudClient implements CreadorSolicitudProveedor{
	
	private final IntegracionProperties props;
	private final WebClient webClient;

	public NuevaSolicitudResponse crearSolicitud(ExperianSolicitudRequest  request) {
		
		String experianBaseUrl = props.getExperian().getBaseUrl();
		String endpointURL = "";
		
		if(request instanceof ExperianSolicitudPFRequest) {
			endpointURL = props.getExperian().getPfUrl();
		} else {
			endpointURL = props.getExperian().getPjUrl();
		}

		//leer atributos yml
        String callbackUrl = props.getCallback().getUrl();
        String user = props.getCredentials().getUser();
        String password = props.getCredentials().getPassword();

		// mapear resto de request de experian
		request.setUserName(user);
		request.setUserPassword(password);
		request.setRequestReference(String.valueOf(System.currentTimeMillis()));
		request.setCallbackUrl(callbackUrl);
		request.setCallbackBearerToken("sacar token");
		
		//llamada Experian
		return webClient
				.post()
				.uri(experianBaseUrl + endpointURL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.onStatus(HttpStatusCode::isError,
					    resp -> WebclientErrorMapper.toAgoraException(resp, "Error llamando a Experian al crear solicitud"))
				.bodyToMono(NuevaSolicitudResponse.class)
				.block();
				
	}

}
