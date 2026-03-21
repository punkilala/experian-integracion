package bs.experian.integracion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bs.experian.integracion.infrastructure.persistence.entity.ColaCustodiaDocumentosEntity;
import bs.experian.integracion.infrastructure.persistence.entity.ColaCustodiaDocumentosPK;

public interface ColaCustodiaDocumentosRepository extends JpaRepository<ColaCustodiaDocumentosEntity, ColaCustodiaDocumentosPK> {
	boolean existsByQueryIdAndDocumentCodeAndNotificationId(String queryId, String documentCode, String notificationId);

}
