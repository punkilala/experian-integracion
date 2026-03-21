package bs.experian.integracion.infrastructure.persistence.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COLA_DESCARGA_DOCUMENTOS")
@IdClass(ColaDescargaDocumentosPK.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ColaDescargaDocumentosEntity {
	
	    @Id
	    @Column(name = "QUERY_ID", length = 60, nullable = false)
	    private String queryId;

	    @Id
	    @Column(name = "DOCUMENT_CODE", length = 100, nullable = false)
	    private String documentCode;
	    
	    @Column(name = "NOTIFICATION_ID", length = 100, nullable = false)
		private String notificationId;

	    @Column(name = "PDF_URL", length = 1000, nullable = false)
	    private String pdfUrl;

	    @Column(name = "JSON_URL", length = 1000, nullable = false)
	    private String jsonUrl;

	    @Lob
	    @Column(name = "PDF_DOCUMENTO")
	    private byte[] pdfDocumento;

	    @Lob
	    @Column(name = "JSON_DOCUMENTO")
	    private String jsonDocumento;

	    @Column(name = "ESTADO_COLA", length = 20, nullable = false)
	    private String estadoCola; // PENDIENTE / EN_PROCESO

	    @Column(name = "INTENTOS", nullable = false)
	    private Integer intentos;

	    @Column(name = "FECHA_ALTA", nullable = false)
	    private OffsetDateTime fechaAlta;

	    @Column(name = "PROCESO_DESDE")
	    private OffsetDateTime procesoDesde;

	    @Column(name = "NEXT_RETRY")
	    private OffsetDateTime nextRetry;
	    
	    @Column(name = "FASE", length = 50)
	    private String fase;
	    
	    @Lob
	    @Column(name = "ERROR_MENSAJE")
	    private String errorMensaje;
}
