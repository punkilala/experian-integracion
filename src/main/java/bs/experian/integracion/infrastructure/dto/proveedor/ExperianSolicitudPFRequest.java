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
public class ExperianSolicitudPFRequest  extends ExperianSolicitudRequest{
	
	// "00" no autónomo, "01" autónomo
	private String personType; 
	// yyyy-MM-dd
    private String birthDate;
    private String postalCode;
    //necesario para NIE
    private String supportNumber;
    private Boolean bypassPinCodeValidation;

}
