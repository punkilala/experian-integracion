package bs.experian.integracion.dto.crearSolicitud;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExperianResponse {
	
	private String queryId;
    private String requestReference;
    private String status;
    private String substatus;
    private String consentUrl;
    private String consentUrlBoxed;

}
