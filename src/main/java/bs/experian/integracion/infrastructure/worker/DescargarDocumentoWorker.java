package bs.experian.integracion.infrastructure.worker;

import java.util.Optional;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

import bs.experian.integracion.infrastructure.persistence.ProcesarDocumentosRepository;
import bs.experian.integracion.infrastructure.persistence.entity.ColaDescargaDocumentosEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DescargarDocumentoWorker {
	
	private final ProcesarDocumentosRepository procesarColaDocumentosRepository;
	private final ProcesadorWorker procesadorWorker;
	
	private volatile long WORKER_DORMIDO = 0;
	
	@Scheduled(fixedDelayString = "15000")
	public void descargarDocumento() {
		//obsoleto uso kafka
		System.out.println("inicio worker"); 
		ColaDescargaDocumentosEntity documento = null;
		
		if (System.currentTimeMillis() < WORKER_DORMIDO) {
	        return;
	    }
		
		try {
			Optional<ColaDescargaDocumentosEntity> documentoOpt = procesarColaDocumentosRepository.reclamar();
			
			//no ha documentos a descargar
			if(documentoOpt.isEmpty()) {
				System.out.println("fin worker");
				return;
			}
			
			documento = documentoOpt.get();
			procesadorWorker.procesar(documento);
			System.out.println("fin worker");
		
		}catch (CannotCreateTransactionException | JDBCConnectionException | DataAccessResourceFailureException 
				|TransientDataAccessResourceException | BadSqlGrammarException | RecoverableDataAccessException e) {
			//caida de bdd
			log.error("BDD no disponible, pausando worker 5 minutos", e);
			WORKER_DORMIDO = System.currentTimeMillis() + 40000;
        } catch (Exception e) {
        	if(null == documento) {
        		 log.error("ERROR INTEGRACION: err inesperado en worker", e);
        		procesadorWorker.gestionErrorWorker(e);
        	}else {
        		log.error("ERROR INTEGRACION: err inesperado en worker para {} - {}", documento.getQueryId(), documento.getDocumentCode(), e);
        		procesarColaDocumentosRepository.reprogramarDescarga(documento);
        	}
        }
		
	}
	
}
