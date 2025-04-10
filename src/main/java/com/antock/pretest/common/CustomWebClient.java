package com.antock.pretest.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomWebClient {

    public <T> Mono<T> get(
            String baseUrl,
            Function<UriBuilder, URI> uriFunction,
            ParameterizedTypeReference<T> elementTypeRef
    ) {
        String uriString = uriFunction.apply(UriComponentsBuilder.fromUriString(baseUrl)).toString();
        return WebClient.builder().baseUrl(baseUrl)
                .build().get()
                .uri(uriFunction)
                .retrieve()
                .bodyToMono(elementTypeRef)
                .doOnNext(logSuccess(uriString))
                .doOnError(logFail(uriString))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(e -> e instanceof WebClientResponseException));
    }

    public <T> Mono<T> get(
            String baseUrl,
            URI uri,
            ParameterizedTypeReference<T> elementTypeRef
    ) {
        return WebClient.builder().baseUrl(baseUrl)
                .build().get()
                .uri(uri)
                .retrieve()
                .bodyToMono(elementTypeRef)
                .doOnNext(logSuccess(uri.toString()))
                .doOnError(logFail(uri.toString()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(e -> e instanceof WebClientResponseException));
    }

    private static Consumer<Throwable> logFail(String uri) {
        return e -> log.error("[{}][{}] {} {} uri={} exception={} message={}",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                Thread.currentThread().getName(),
                "FAIL",
                "GET",
                uri,
                e.getClass().getName(),
                e.getMessage()
        );
    }

    private static <T> Consumer<T> logSuccess(String uri) {
        return res -> log.info("[{}][{}] {} {} uri={}",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                Thread.currentThread().getName(),
                "SUCCESS",
                "GET",
                uri
        );
    }
}
