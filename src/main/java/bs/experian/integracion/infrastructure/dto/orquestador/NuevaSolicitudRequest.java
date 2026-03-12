package bs.experian.integracion.infrastructure.dto.orquestador;



import bs.experian.integracion.domain.enums.DomainEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NuevaSolicitudRequest {
	
/////////// COMUNES
private String phoneNumber;
private Integer belenderCircuit;
private String documentationPack;

///////////	PF 
//noAutonomo= 00 autonomo=01
private String personType; 
//idfiscal PF o Representante PJ
private String personId;
private String firstName;
private String firstSurname;
private String secondSurname;
private String birthDate;
private String supportNumber;
private String personIdExpirationDate;


////////////PJ
private String email;
private String personIdFrontPhoto;
private String personIdBackPhoto;
private String companyId;
private String companyName;


/////////// SOLO ORQUESTADOR
private DomainEnum.TipoPersona personCategory;
private String OfficeCode;

}
	
	
