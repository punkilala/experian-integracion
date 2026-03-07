package bs.experian.integracion.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import bs.experian.integracion.infrastructure.dto.orquestador.NuevaSolicitudRequest;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianSolicitudPFRequest;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianSolicitudPJRequest;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExperianMapper {
	
	ExperianSolicitudPFRequest pfToExperianSolicitud  (NuevaSolicitudRequest nuevaSolicitudRequest);	
	ExperianSolicitudPJRequest pjToExperianSolicitud  (NuevaSolicitudRequest nuevaSolicitudRequest);

}
