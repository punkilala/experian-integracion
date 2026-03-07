package bs.experian.integracion.infrastructure.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
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
            .filter(logRequestResponse())
            .build();
    }
	
	private ExchangeFilterFunction logRequestResponse() {
	    return (request, next) -> {
	        long start = System.currentTimeMillis();

	        log.info("Integracion Request: {} {}", request.method(), request.url());

	        return next.exchange(request)
	        	    .doOnNext(response -> {
	        	        long duration = System.currentTimeMillis() - start;
	        	        log.info("Integracion Response Status: {} {} ms",
	        	                 response.statusCode(),
	        	                 duration);
	        	    })
	        	    .doOnError(error -> {
	        	        long duration = System.currentTimeMillis() - start;
	        	        log.error("Integracion Error after {} ms: {}",
	        	                  duration,
	        	                  error.getMessage());
	         });
	    };
	}

}
