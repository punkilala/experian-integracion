package bs.experian.integracion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bs.experian.integracion.infrastructure.persistence.entity.ColaDescargaDocumentosEntity;
import bs.experian.integracion.infrastructure.persistence.entity.ColaDescargaDocumentosPK;

@Repository
public interface ColaDescargaDocumentosRepository extends JpaRepository<ColaDescargaDocumentosEntity, ColaDescargaDocumentosPK> {

}
