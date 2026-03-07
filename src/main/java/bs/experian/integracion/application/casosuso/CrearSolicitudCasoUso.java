package bs.experian.integracion.application.casosuso;

import static bs.experian.integracion.infrastructure.constants.ExperianConstats.BELENDER_PF;
import static bs.experian.integracion.infrastructure.constants.ExperianConstats.BELENDER_PJ;

import org.springframework.stereotype.Service;

import bs.experian.integracion.application.proveedor.CreadorSolicitudProveedor;
import bs.experian.integracion.infrastructure.dto.orquestador.NuevaSolicitudRequest;
import bs.experian.integracion.infrastructure.dto.orquestador.NuevaSolicitudResponse;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianSolicitudPFRequest;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianSolicitudPJRequest;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianSolicitudRequest;
import bs.experian.integracion.infrastructure.mappers.ExperianMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrearSolicitudCasoUso {
	private final CreadorSolicitudProveedor creadorSolicitudProveedor;
	private final ExperianMapper experianMapper;
	
	public NuevaSolicitudResponse ejecutar (NuevaSolicitudRequest nuevaSolicitudRequest) {
		//mapear request experian
		ExperianSolicitudRequest experianSolicitudRequest = null;
		switch (nuevaSolicitudRequest.getPersonCategory()) {
		    case PF -> {
		        ExperianSolicitudPFRequest pfRequest = experianMapper.pfToExperianSolicitud(nuevaSolicitudRequest);
		        pfRequest.setBelenderCircuit(BELENDER_PF);
		        experianSolicitudRequest = pfRequest;
		       
		    }

		    case PJ -> {
		        ExperianSolicitudPJRequest pjRequest = experianMapper.pjToExperianSolicitud(nuevaSolicitudRequest);
		        pjRequest.setBelenderCircuit(BELENDER_PJ);
		        experianSolicitudRequest = pjRequest;
		    }

		    default -> throw new IllegalArgumentException(
		            "Tipo persona no soportado: " + nuevaSolicitudRequest.getPersonCategory()
		    );
		}
		return creadorSolicitudProveedor.crearSolicitud(experianSolicitudRequest);
		
	}
}
