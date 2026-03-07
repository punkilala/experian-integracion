package bs.experian.integracion.application.proveedor;

import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;

public interface ReenviadorEventoProveedor {
	
	void reenviarEvento (ExperianWebhookEvent request);

}
