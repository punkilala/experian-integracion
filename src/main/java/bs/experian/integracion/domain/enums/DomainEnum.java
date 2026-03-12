package bs.experian.integracion.domain.enums;

public final class DomainEnum {

	private DomainEnum() {
        throw new UnsupportedOperationException(DomainEnum.class.getName() + " no instanciable");
    }
	
	//TIPO PERSONA
	 public enum TipoPersona {

        PF("PERSONA_FISICA"),
        PJ("PERSONA_JURIDICA");

        private final String descripcion;

        TipoPersona(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

}