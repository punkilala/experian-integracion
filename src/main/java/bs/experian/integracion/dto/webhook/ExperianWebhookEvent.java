package bs.experian.integracion.dto.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ExperianWebhookEvent {
    private String queryId;
    private String notificationId;
    private String eventType;
    private JsonNode eventData;
}
