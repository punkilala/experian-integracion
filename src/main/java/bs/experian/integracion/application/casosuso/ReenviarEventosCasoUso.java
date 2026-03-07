package bs.experian.integracion.application.casosuso;

import org.springframework.stereotype.Service;

import bs.experian.integracion.application.proveedor.ReenviadorEventoProveedor;
import bs.experian.integracion.infrastructure.dto.proveedor.ExperianWebhookEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReenviarEventosCasoUso {
	
	private final ReenviadorEventoProveedor reenviadorEventoProveedor;
	
	public void ejecutar(ExperianWebhookEvent request) {
		reenviadorEventoProveedor.reenviarEvento(request);
	}

}
