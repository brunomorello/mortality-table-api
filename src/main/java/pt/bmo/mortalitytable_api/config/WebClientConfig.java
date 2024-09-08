package pt.bmo.mortalitytable_api.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        final HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(createSslContext()))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 400000)
                .responseTimeout(Duration.ofMillis(400000))
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(500000, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(00000, TimeUnit.MILLISECONDS)));

        return builder.clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

    private SslContext createSslContext() {
        SslContext sslContext = null;
        try {
            sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException e) {
            log.error("Error to generate InsecureTrustManager: {}", e.getMessage());
        }
        return sslContext;
    }
}
