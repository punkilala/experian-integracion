package bs.experian.integracion.infrastructure.dto.orquestador;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DescargaDocumentoEvent {
	
	private String queryId;
	private String notificationId;
	private String eventType;
	private DescargaDocumentoEventData eventData;
	
}
