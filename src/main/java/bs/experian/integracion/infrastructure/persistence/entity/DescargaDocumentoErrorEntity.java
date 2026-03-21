package bs.experian.integracion.infrastructure.persistence.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DESCARGA_DOCUMENTOS_ERROR")
@Getter @Setter
@NoArgsConstructor 
@AllArgsConstructor
public class DescargaDocumentoErrorEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ERROR", nullable = false)
    private Long idError;

    @Column(name = "QUERY_ID", nullable = false, length = 60)
    private String queryId;

    @Column(name = "DOCUMENT_CODE", nullable = false, length = 100)
    private String documentCode;
    
    @Column(name = "NOTIFICATION_ID", length = 100, nullable = false)
	private String notificationId;

    @Column(name = "INTENTO", nullable = false)
    private Integer intento;

    @Column(name = "TIPO_DOCUMENTO", length = 20)
    private String tipoDocumento;

    @Column(name = "ORIGEN_ERROR", nullable = false, length = 1000)
    private String origenError;

    @Lob
    @Column(name = "MENSAJE_ERROR")
    private String mensajeError;

    @Column(name = "FECHA_ERROR", nullable = false)
    private OffsetDateTime fechaError;
}
