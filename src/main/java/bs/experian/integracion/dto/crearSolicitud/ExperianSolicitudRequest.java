package bs.experian.integracion.dto.crearSolicitud;

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
public abstract  class ExperianSolicitudRequest {
	//Autenticación
    private String userName;
    private String userPassword;

    //Identificador consulta
    private String requestReference;

    //Datos persona / representante
    private String personId;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String firstSurname;
    private String secondSurname;

    private String personIdFrontPhoto;
    private String personIdBackPhoto;
    private String identificationDocument;
    // yyyy-MM-dd
    private String personIdExpirationDate;

    private Integer belenderCircuit;

    private String evidenceLocation;
    private String evidence;
    //yyyy-MM-dd
    private String evidenceDate;

    private String certificadoValidacion;

    //Configuración técnica
    private String documentationPack;
    private Boolean createRequestAsynchronously;

    private String callbackUrl;
    private String callbackBearerToken;

    private Boolean powerActivate;
    private Boolean validationid;
    private Boolean rmcid;
    private Boolean flowControl;
    
    private String language;

}
