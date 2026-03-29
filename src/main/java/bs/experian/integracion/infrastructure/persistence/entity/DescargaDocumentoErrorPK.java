package bs.experian.integracion.infrastructure.persistence.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DescargaDocumentoErrorPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String queryId;
    private String documentCode;
    private String tipoDocumento;
}
