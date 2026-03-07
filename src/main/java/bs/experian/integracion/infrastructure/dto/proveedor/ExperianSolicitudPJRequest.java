package bs.experian.integracion.infrastructure.dto.proveedor;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperianSolicitudPJRequest extends ExperianSolicitudRequest {
	private String companyId;
    private String companyName;

}
