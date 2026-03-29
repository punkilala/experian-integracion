package bs.experian.integracion.application;

import org.springframework.stereotype.Service;

import bs.experian.integracion.domain.model.DescargaDocumentoModel;
import bs.experian.integracion.infrastructure.dto.orquestador.DescargaDocumentoEvent;
import bs.experian.integracion.infrastructure.persistence.ProcesarDocumentosRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DescargarDocumentosService {
	
	private final ProcesarDocumentosRepository procesarDocumentosRepository;
	
	//Encolar documentos a descargar
	public void descargarDocumento (DescargaDocumentoEvent request) {
	
		
	}
	

}
