package bs.experian.integracion.application;

import org.springframework.stereotype.Service;

import bs.experian.integracion.domain.model.DescargaDocumentoModel;
import bs.experian.integracion.infrastructure.dto.orquestador.DescargaDocumentoRequest;
import bs.experian.integracion.infrastructure.mappers.DomainMapper;
import bs.experian.integracion.infrastructure.persistence.ProcesarDocumentosRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DescargarDocumentosService {
	
	private final DomainMapper domainMapper;
	private final ProcesarDocumentosRepository procesarDocumentosRepository;
	
	//Encolar documentos a descargar
	public void descargarDocumento (DescargaDocumentoRequest request) {
		DescargaDocumentoModel descargaDocumentoModel = domainMapper.requestToDescargaDocumentoModel(request);
		procesarDocumentosRepository.encolarDocumento(descargaDocumentoModel);
		
	}
	

}
