package bs.experian.integracion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bs.experian.integracion.infrastructure.persistence.entity.DescargaDocumentoErrorEntity;

public interface DescargaDocumentoErrorRepository extends JpaRepository<DescargaDocumentoErrorEntity, Long> {

}
