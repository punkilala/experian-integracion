package bs.experian.integracion.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import bs.experian.integracion.domain.model.DescargaDocumentoModel;
import bs.experian.integracion.infrastructure.dto.orquestador.DescargaDocumentoRequest;
import bs.experian.integracion.infrastructure.persistence.entity.ColaDescargaDocumentosEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DomainMapper {
	DescargaDocumentoModel requestToDescargaDocumentoModel (DescargaDocumentoRequest request);
	DescargaDocumentoModel colaDescargaDocumentosEntityToDescargaDocumentoModel (ColaDescargaDocumentosEntity entithy);
	ColaDescargaDocumentosEntity descargaDocumentoModelToColaDescargaDocumentosEntity (DescargaDocumentoModel model);
}
