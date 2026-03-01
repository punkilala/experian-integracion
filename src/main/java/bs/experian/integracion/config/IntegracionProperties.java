package bs.experian.integracion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api")
public class IntegracionProperties {

    private Experian experian;
    private Callback callback;
    private Credentials credentials;
    private Orquestador orquestador;

    @Getter
    @Setter
    public static class Experian {

        private String baseUrl;
        private String pfUrl;
        private String pjUrl;
        private Integer timeoutrequest;
        private Integer timeoutresponse;
    }

    @Getter
    @Setter
    public static class Callback {

        private String url;
    }

    @Getter
    @Setter
    public static class Credentials {

        private String user;
        private String password;
    }
    
    @Getter
    @Setter
    public static class Orquestador{
    	private String baseUrl;
    	private String eventosUrl;
    }
}