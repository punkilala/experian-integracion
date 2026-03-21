package bs.experian.integracion.infrastructure.dto.proveedor;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExperianWebhookEvent {
	@NotBlank (message = "campo requerido")
    private String queryId;
	
	@NotBlank (message = "campo requerido")
    private String notificationId;
	
    private String origen;
    
    @NotBlank (message = "campo requerido")
    private String eventType;
    
    @NotNull (message = "campo requerido")
    private JsonNode eventData;
}
