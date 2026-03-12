package bs.experian.integracion.infrastructure.persistence.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColaDescargaDocumentosPK implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String queryId;
    private String documentCode;

}
