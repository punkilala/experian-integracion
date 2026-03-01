package bs.experian.integracion.service;

import bs.experian.integracion.dto.webhook.ExperianWebhookEvent;

public interface ReenvioEventosService {
	void reenvioToOrquestador (ExperianWebhookEvent request);
}
