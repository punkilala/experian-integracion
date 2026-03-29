package bs.experian.integracion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bs.experian.integracion.infrastructure.persistence.entity.DocumentoPendienteCustodiaEntity;
import bs.experian.integracion.infrastructure.persistence.entity.DocumentoPendienteCustodiaPK;

public interface DocumentoPendienteCustodiaRepository extends JpaRepository<DocumentoPendienteCustodiaEntity, DocumentoPendienteCustodiaPK>{

}
