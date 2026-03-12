package bs.experian.integracion.infrastructure.worker;

import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import bs.experian.integracion.infrastructure.persistence.ProcesarDocumentosRepository;
import bs.experian.integracion.infrastructure.persistence.entity.ColaDescargaDocumentosEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DescargarDocumentoWorker {
	
	private final ProcesarDocumentosRepository procesarColaDocumentosRepository;
	private final ProcesadorDescargaDocumento procesadorDescargaDocumento;
	
	@Scheduled(fixedDelayString = "15000")
	public void descargarDocumento() {
		System.out.println("inicio worker");
		Optional<ColaDescargaDocumentosEntity> documentoOpt = procesarColaDocumentosRepository.reclamar();
		
		//no ha documentos a descargar
		if(documentoOpt.isEmpty()) {
			System.out.println("fin worker");
			return;
		}
		
		ColaDescargaDocumentosEntity documento = documentoOpt.get();
		
		try {
			procesadorDescargaDocumento.procesar(documento);
			System.out.println("fin worker");
        } catch (Exception e) {
            log.error("ERROR INTEGRACION: err inesperado en worker para {} - {}", documento.getQueryId(), documento.getDocumentCode(), e);
        }
		
	}

}
