package bs.experian.integracion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DescargaDocumentoModel {	
	private String queryId;
	private String documentCode;
	private String notificationId;
	
	private String pdfUrl;
	private byte[] pdfDocument;
	
	private String jsonUrl;
	private String jsonDocument;
	
	private Integer intentos;
	
	private String tipoDocumento;
	
	private String origenError;
	private String errorMensaje;
	
		
}
