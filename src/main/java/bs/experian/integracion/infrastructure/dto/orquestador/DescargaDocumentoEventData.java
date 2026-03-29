package bs.experian.integracion.infrastructure.dto.orquestador;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescargaDocumentoEventData {
    private String status;
    private String substatus;
    private String documentCode;

    private String pdfEstado;
    private String jsonEstado;

}
