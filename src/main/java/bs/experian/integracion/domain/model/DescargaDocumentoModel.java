package bs.experian.integracion.domain.model;

import java.time.OffsetDateTime;

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
	private byte[] pdfDocumento;
	
	private String jsonUrl;
	private String jsonDocumento;
	
	private String tipoDocumento;
	private String errorMensaje;
	
	private int intentos;
	private OffsetDateTime siguienteReintento;
		
}
