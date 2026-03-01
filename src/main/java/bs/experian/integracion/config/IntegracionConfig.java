package bs.experian.integracion.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
public class IntegracionConfig {
	

	@Bean
    public WebClient webClient(IntegracionProperties props) {

        Integer timeoputrequest = props.getExperian().getTimeoutrequest();
        Integer timepoutresponse = props.getExperian().getTimeoutresponse();

        if (timeoputrequest == null) timeoputrequest = 5000;
        if (timepoutresponse == null) timepoutresponse = 10000;

        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoputrequest)
            .responseTimeout(Duration.ofMillis(timepoutresponse));

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

}
