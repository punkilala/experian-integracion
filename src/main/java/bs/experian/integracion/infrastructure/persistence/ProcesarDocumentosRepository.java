package bs.experian.integracion.infrastructure.persistence;

import java.time.OffsetDateTime;
import org.springframework.stereotype.Repository;

import bs.experian.integracion.domain.model.DescargaDocumentoModel;
import bs.experian.integracion.infrastructure.persistence.entity.DescargaDocumentoErrorEntity;
import bs.experian.integracion.infrastructure.persistence.entity.DocumentoPendienteCustodiaEntity;
import bs.experian.integracion.infrastructure.persistence.entity.DocumentoPendienteCustodiaPK;
import bs.experian.integracion.infrastructure.persistence.repository.DescargaDocumentoErrorRepository;
import bs.experian.integracion.infrastructure.persistence.repository.DocumentoPendienteCustodiaRepository;
import bs.experian.integracion.infrastructure.utils.IntegracionUtils;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProcesarDocumentosRepository {

	private final DescargaDocumentoErrorRepository descargaDocumentoErrorRepository;
	private final DocumentoPendienteCustodiaRepository documentoPendienteCustodiaRepository;


	
	/**
	 * Recuperar si ya existe el docummento pendiente de descarga
	 * @param model
	 */
	public void recuperarDoocumentosPteDescarga (DescargaDocumentoModel model) {
		DocumentoPendienteCustodiaEntity entity = documentoPendienteCustodiaRepository.findById(
				new DocumentoPendienteCustodiaPK(model.getQueryId(), model.getDocumentCode()))
					.orElse(null);
		
		model.setIntentos(1);
		if (null != entity) {
			model.setPdfDocument(entity.getPdfDocument());
			model.setJsonDocument(entity.getJsonDocument());
			model.setIntentos(entity.getIntentos() + 1);
		}
	}
	
	/**
	 * Guardar documentos descargados en tabla temporal
	 */
	public void guardarDocumentosEnTemporal (DescargaDocumentoModel doc) {
		DocumentoPendienteCustodiaEntity entity = new DocumentoPendienteCustodiaEntity();
		entity.setQueryId(doc.getQueryId());
		entity.setDocumentCode(doc.getDocumentCode());
		entity.setNotificationId(doc.getNotificationId());
		entity.setPdfDocument(doc.getPdfDocument());
		entity.setJsonDocument(doc.getJsonDocument());
		entity.setEstatus("DESCARGA");
		entity.setIntentos(doc.getIntentos());
		entity.setFechaAlta(OffsetDateTime.now());
		
		documentoPendienteCustodiaRepository.save(entity);	
	}
	
	
	/**
	 * guardar errores de descarga de documentos
	 */
	public void registrarErrorDescarga(DescargaDocumentoModel doc, Exception e) {
		DescargaDocumentoErrorEntity entity = new DescargaDocumentoErrorEntity();
		
		entity.setQueryId(doc.getQueryId());
		entity.setDocumentCode(doc.getDocumentCode());
		entity.setNotificationId(doc.getNotificationId());
		entity.setIntentos(doc.getIntentos());
		entity.setTipoDocumento(doc.getTipoDocumento());
		entity.setOrigenError(e.getMessage());
		entity.setMensajeError(IntegracionUtils.stackTraceToString(e, 12));
		entity.setFechaError(OffsetDateTime.now());
		
		descargaDocumentoErrorRepository.save(entity);
		
	}
	


}
