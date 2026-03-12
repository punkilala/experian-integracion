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
@Table(name = "COLA_CUSTODIA_DOCUMENTOS")
@IdClass(ColaCustodiaDocumentosPK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColaCustodiaDocumentosEntity {
	@Id
    @Column(name = "QUERY_ID", length = 60, nullable = false)
    private String queryId;

    @Id
    @Column(name = "DOCUMENT_CODE", length = 100, nullable = false)
    private String documentCode;

    @Column(name = "ESTADO", length = 20, nullable = false)
    private String estado; // BLOQUEADO, AUTORIZADO, IN_PROGRESS

    @Column(name = "INTENTOS", nullable = false)
    private Integer intentos;

    @Column(name = "NEXT_RETRY")
    private OffsetDateTime nextRetry;

    @Column(name = "PROCESO_DESDE")
    private OffsetDateTime procesoDesde;

    @Column(name = "FECHA_ALTA", nullable = false, updatable = false)
    private OffsetDateTime fechaAlta;

    @Lob
    @Column(name = "PDF_BINARIO", nullable = false)
    private byte[] pdfBinario;

}
