package bs.experian.integracion.service;

import bs.experian.integracion.dto.crearSolicitud.ExperianResponse;
import bs.experian.integracion.dto.crearSolicitud.NuevaSolicitudRequest;

public interface NuevaSolicitudService {
	ExperianResponse crearSolicitud (NuevaSolicitudRequest nuevaSolicitudRequest);
}
