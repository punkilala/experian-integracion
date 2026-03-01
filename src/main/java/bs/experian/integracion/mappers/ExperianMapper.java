package bs.experian.integracion.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import bs.experian.integracion.dto.crearSolicitud.ExperianSolicitudPFRequest;
import bs.experian.integracion.dto.crearSolicitud.ExperianSolicitudPJRequest;
import bs.experian.integracion.dto.crearSolicitud.NuevaSolicitudRequest;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExperianMapper {
	
	ExperianSolicitudPFRequest pfToExperianSolicitud  (NuevaSolicitudRequest nuevaSolicitudRequest);	
	ExperianSolicitudPJRequest pjToExperianSolicitud  (NuevaSolicitudRequest nuevaSolicitudRequest);

}
