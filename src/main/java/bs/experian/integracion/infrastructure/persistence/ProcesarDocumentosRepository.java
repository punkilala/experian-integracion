package bs.experian.integracion.infrastructure.persistence;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import bs.experian.integracion.domain.model.DescargaDocumentoModel;
import bs.experian.integracion.infrastructure.mappers.DomainMapper;
import bs.experian.integracion.infrastructure.persistence.entity.ColaCustodiaDocumentosEntity;
import bs.experian.integracion.infrastructure.persistence.entity.ColaCustodiaDocumentosPK;
import bs.experian.integracion.infrastructure.persistence.entity.ColaDescargaDocumentosEntity;
import bs.experian.integracion.infrastructure.persistence.entity.ColaDescargaDocumentosPK;
import bs.experian.integracion.infrastructure.persistence.entity.DescargaDocumentoErrorEntity;
import bs.experian.integracion.infrastructure.persistence.repository.ColaCustodiaDocumentosRepository;
import bs.experian.integracion.infrastructure.persistence.repository.ColaDescargaDocumentosRepository;
import bs.experian.integracion.infrastructure.persistence.repository.DescargaDocumentoErrorRepository;
import bs.experian.integracion.infrastructure.utils.IntegracionUtils;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProcesarDocumentosRepository {
	private final ColaDescargaDocumentosRepository colaDescargaDocumentosRepository;
	private final DescargaDocumentoErrorRepository descargaDocumentoErrorRepository;
	private final ColaCustodiaDocumentosRepository colaCustodiaDocumentosRepository;
	private final JdbcTemplate template;
	private final DomainMapper domainMapper;
	
	/**
	 * encolar documento a descargar si no existe uno ya con el mismo nootificationId
	 * @param request
	 */
	public void encolarDocumento(DescargaDocumentoModel model) {
		
        ColaDescargaDocumentosPK pk = new ColaDescargaDocumentosPK(
        			model.getQueryId(),
        			model.getDocumentCode()
        		);
        
        ColaDescargaDocumentosEntity entity = 
        		colaDescargaDocumentosRepository.findById(pk)
        			.orElseGet(()-> new ColaDescargaDocumentosEntity());
        
        if (!Objects.equals(model.getNotificationId(), entity.getNotificationId())) {
        	entity = domainMapper.descargaDocumentoModelToColaDescargaDocumentosEntity(model);
        	entity.setEstadoCola("PENDIENTE");
            entity.setIntentos(0);
            entity.setFechaAlta(OffsetDateTime.now());
            
            colaDescargaDocumentosRepository.save(entity);  
        }
    }
	
	/**
	 * reclamar documento a descargar con control de concurrencia
	 * @return
	 */
	@Transactional
	public Optional<ColaDescargaDocumentosEntity> reclamar() {
		OffsetDateTime ahora = OffsetDateTime.now();
	    OffsetDateTime limiteProceso = ahora.minusMinutes(15);
	    
	    String sqlUpdate = """
        UPDATE COLA_DESCARGA_DOCUMENTOS
        SET ESTADO_COLA = 'EN_PROCESO',
            PROCESO_DESDE = ?
        WHERE QUERY_ID = ?
          AND DOCUMENT_CODE = ?
        """;

	    String sqlBloqueo = """
        SELECT *
        FROM COLA_DESCARGA_DOCUMENTOS
        WHERE (QUERY_ID, DOCUMENT_CODE) IN (
            SELECT QUERY_ID, DOCUMENT_CODE
            FROM (
                SELECT QUERY_ID, DOCUMENT_CODE
                FROM COLA_DESCARGA_DOCUMENTOS
                WHERE (
                        (ESTADO_COLA = 'PENDIENTE'
                         AND (NEXT_RETRY IS NULL OR NEXT_RETRY <= ?))
                     OR (ESTADO_COLA = 'EN_PROCESO'
                         AND PROCESO_DESDE < ?)
                )
                ORDER BY FECHA_ALTA
            )
            WHERE ROWNUM = 1
        )
        FOR UPDATE SKIP LOCKED
        """;

	    return template.query(
	        sqlBloqueo,
	        ps -> {
	            ps.setObject(1, ahora);
	            ps.setObject(2, limiteProceso);
	        },
	        rs -> {

	            if (!rs.next()) {
	                return Optional.empty();
	            }

	            ColaDescargaDocumentosEntity doc = new ColaDescargaDocumentosEntity();
	            doc.setQueryId(rs.getString("QUERY_ID"));
	            doc.setDocumentCode(rs.getString("DOCUMENT_CODE"));
	            doc.setNotificationId(rs.getString("NOTIFICATION_ID"));
	            doc.setPdfUrl(rs.getString("PDF_URL"));
	            doc.setJsonUrl(rs.getString("JSON_URL"));
	            doc.setEstadoCola(rs.getString("ESTADO_COLA"));
	            doc.setIntentos(rs.getInt("INTENTOS"));
	            doc.setFechaAlta(rs.getObject("FECHA_ALTA", OffsetDateTime.class));
	            doc.setPdfDocumento(rs.getBytes("PDF_DOCUMENTO"));
	            doc.setJsonDocumento(rs.getString("JSON_DOCUMENTO"));

	            // marcar como EN_PROCESO
	            template.update(
	                sqlUpdate,
	                ahora,
	                doc.getQueryId(),
	                doc.getDocumentCode()
	            );

	            return Optional.of(doc);
	        }
	    );
	}
	
	/**
	 * guardar documento temporalmente
	 * @param doc
	 */
	@Transactional
	public void guardarContenido(ColaDescargaDocumentosEntity  doc) {
		
		colaDescargaDocumentosRepository.save(doc);

    }
	
	/**
	 * reprogramar descarga documento
	 * @param doc
	 */
	public void reprogramarDescarga(ColaDescargaDocumentosEntity entity) {
		
		 OffsetDateTime siguiente = OffsetDateTime.now().plus(1, ChronoUnit.HOURS);
	    entity.setNextRetry(siguiente);
		entity.setEstadoCola("PENDIENTE");
		entity.setProcesoDesde(null);

	    colaDescargaDocumentosRepository.save(entity);
	}
	
	/**
	 * guardar errores de descarga de documentos
	 */
	public void registrarErrorDescarga(ColaDescargaDocumentosEntity doc, Exception e) {
		DescargaDocumentoErrorEntity entity = new DescargaDocumentoErrorEntity();
		
		entity.setQueryId(doc.getQueryId());
		entity.setDocumentCode(doc.getDocumentCode());
		entity.setNotificationId(doc.getNotificationId());
		entity.setIntento(doc.getIntentos());
		entity.setTipoDocumento(doc.getFase());
		entity.setOrigenError(e.getMessage());
		entity.setMensajeError(IntegracionUtils.stackTraceToString(e, 12));
		entity.setFechaError(OffsetDateTime.now());
		
		descargaDocumentoErrorRepository.save(entity);
		
	}
	

	
	/**
	 * borrar de cola documento descargado
	 * @param doc
	 */
	public void borrarColaWorker (ColaDescargaDocumentosEntity doc) {
		// borrar documento cola de descarga
		colaDescargaDocumentosRepository.deleteById( new ColaDescargaDocumentosPK(
					doc.getQueryId(),
					doc.getDocumentCode()
				));
	}
	
	/**
	 * mandar a custodia el documento pdf
	 * @param doc
	 */
	public void pdfToCustodia (ColaDescargaDocumentosEntity doc) {
		ColaCustodiaDocumentosEntity  custodiaEntity = new ColaCustodiaDocumentosEntity();
		custodiaEntity.setQueryId(doc.getQueryId());
		custodiaEntity.setDocumentCode(doc.getDocumentCode());
		custodiaEntity.setNotificationId(doc.getNotificationId());
		custodiaEntity.setEstado("BLOQUEADO");
		custodiaEntity.setResultadoCustodia(null);
		custodiaEntity.setIntentos(0);
		custodiaEntity.setNextRetry(null);
		custodiaEntity.setProcesoDesde(null);
		custodiaEntity.setFechaAlta(OffsetDateTime.now());
		custodiaEntity.setPdfBinario(doc.getPdfDocumento());
		
		colaCustodiaDocumentosRepository.save(custodiaEntity);
	}

}
