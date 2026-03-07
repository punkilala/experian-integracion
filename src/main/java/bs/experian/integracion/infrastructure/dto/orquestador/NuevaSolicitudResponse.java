package bs.experian.integracion.infrastructure.dto.orquestador;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NuevaSolicitudResponse {
	
	private String queryId;
    private String requestReference;
    private String status;
    private String substatus;
    private String consentUrl;
    private String consentUrlBoxed;

}
