package bs.experian.integracion.infrastructure.dto.proveedor;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ExperianWebhookEvent {
    private String queryId;
    private String notificationId;
    private String eventType;
    private JsonNode eventData;
}
