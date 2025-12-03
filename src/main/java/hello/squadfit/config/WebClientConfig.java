package hello.squadfit.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    // 공통 HttpClient (타임아웃 + 커넥션 풀)
    private HttpClient createHttpClient() {

        // 1) 커넥션 풀 (고급)
        ConnectionProvider provider = ConnectionProvider.builder("fixed")
                .maxConnections(100) // 커넥션 풀 최대 개수
                .pendingAcquireMaxCount(2000) // 대기 요청 수
                .pendingAcquireTimeout(Duration.ofSeconds(5))
                .maxIdleTime(Duration.ofSeconds(20))
                .build();

        // 2) 실제 HttpClient 생성
        return HttpClient.create(provider)
                .responseTimeout(Duration.ofSeconds(5))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(5))
                                .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(5))
                );
    }

    // 네이버 SENS 전용 WebClient
    @Bean
    @Qualifier("sensWebClient")
    public WebClient sensWebClient() {

        return WebClient.builder()
                .baseUrl("https://sens.apigw.ntruss.com")
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // 일반 API 호출용 WebClient
    @Bean
    public WebClient webClient() {

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
