package bs.experian.integracion.application.proveedor;

import bs.experian.integracion.infrastructure.dto.orquestador.NuevaSolicitudResponse;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianSolicitudRequest;

public interface CreadorSolicitudProveedor {
	NuevaSolicitudResponse crearSolicitud(ExperianSolicitudRequest request);
}
