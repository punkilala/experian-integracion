package bs.experian.integracion.service;

import static bs.experian.integracion.constants.ExperianConstats.BELENDER_PF;
import static bs.experian.integracion.constants.ExperianConstats.BELENDER_PJ;


import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import bs.experian.integracion.config.IntegracionProperties;
import bs.experian.integracion.dto.crearSolicitud.ExperianResponse;
import bs.experian.integracion.dto.crearSolicitud.ExperianSolicitudPFRequest;
import bs.experian.integracion.dto.crearSolicitud.ExperianSolicitudPJRequest;
import bs.experian.integracion.dto.crearSolicitud.ExperianSolicitudRequest;
import bs.experian.integracion.dto.crearSolicitud.NuevaSolicitudRequest;
import bs.experian.integracion.exceptions.WebclientErrorMapper;
import bs.experian.integracion.mappers.ExperianMapper;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NuevaSolicitudServiceImpl implements NuevaSolicitudService {
	
	private final ExperianMapper experianMapper;
	private final IntegracionProperties props;
	private final WebClient webClient;

	@Override
	public ExperianResponse crearSolicitud(NuevaSolicitudRequest nuevaSolicitudRequest) {
		
		String experianBaseUrl = props.getExperian().getBaseUrl();
		//mapear request experian
		String personaURL = "";
		ExperianSolicitudRequest experianSolicitudRequest = null;
		switch (nuevaSolicitudRequest.getPersonCategory()) {
		    case PF -> {
		        ExperianSolicitudPFRequest pfRequest = experianMapper.pfToExperianSolicitud(nuevaSolicitudRequest);
		        pfRequest.setBelenderCircuit(BELENDER_PF);
		        personaURL = props.getExperian().getPfUrl();
		        experianSolicitudRequest = pfRequest;
		       
		    }

		    case PJ -> {
		        ExperianSolicitudPJRequest pjRequest = experianMapper.pjToExperianSolicitud(nuevaSolicitudRequest);
		        pjRequest.setBelenderCircuit(BELENDER_PJ);
		        personaURL = props.getExperian().getPjUrl();
		        experianSolicitudRequest = pjRequest;
		    }

		    default -> throw new IllegalArgumentException(
		            "Tipo persona no soportado: " + nuevaSolicitudRequest.getPersonCategory()
		    );
		}
		
		//leer atributos yml
        String callbackUrl = props.getCallback().getUrl();
        String user = props.getCredentials().getUser();
        String password = props.getCredentials().getPassword();

		// mapear resto de request de experian
		experianSolicitudRequest.setUserName(user);
		experianSolicitudRequest.setUserPassword(password);
		experianSolicitudRequest.setRequestReference(String.valueOf(System.currentTimeMillis()));
		experianSolicitudRequest.setCallbackUrl(callbackUrl);
		experianSolicitudRequest.setCallbackBearerToken("sacar token");
		
		//llamada Experian
		ExperianResponse response = webClient
				.post()
				.uri(experianBaseUrl + personaURL)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(experianSolicitudRequest)
				.retrieve()
				.onStatus(HttpStatusCode::isError,
					    resp -> WebclientErrorMapper.toAgoraException(resp, "Error llamando a Experian al crear solicitud"))
				.bodyToMono(ExperianResponse.class)
				.block();
				
		return response;
	}

}
