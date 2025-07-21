// src/main/java/com/interview/service_p/service/RestImpl.java
package com.interview.service_p.service;

import com.interview.service_p.config.RestConfigProperties;
import com.interview.service_p.config.RestConfigProperties.ApiProviderProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
public class RestImpl {

    private static final Logger log = LoggerFactory.getLogger(RestImpl.class);
    private final Map<String, WebClient> webClients;
    private final RestConfigProperties restConfigProperties;

    public RestImpl(WebClient.Builder webClientBuilder,
                    RestConfigProperties restConfigProperties) {
        this.restConfigProperties = restConfigProperties;
        this.webClients = new java.util.HashMap<>();

        restConfigProperties.getProviders().forEach((providerName, props) -> {
            WebClient client = webClientBuilder
                    .baseUrl(props.getBaseUrl())
                    .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(
                            HttpClient.create().responseTimeout(Duration.ofMillis(props.getTimeoutMs()))
                    ))
                    .defaultStatusHandler(HttpStatusCode::isError, response ->
                            response.createException().flatMap(ex -> {
                                log.error("Error response from {}: Status {} - {}", providerName, response.statusCode(), ex.getMessage());
                                return Mono.error(ex);
                            })
                    )
                    .build();

            webClients.put(providerName, client);
            log.info("Initialized WebClient for provider: {} with base URL: {}", providerName, props.getBaseUrl());
        });
    }

    public <T> Optional<T> executeGet(String providerName, Map<String, String> resourcePathVariables, Map<String, String> queryParams, Class<T> responseType) {
        ApiProviderProperties providerProps = restConfigProperties.getProviders().get(providerName);
        WebClient webClient = webClients.get(providerName);

        if (providerProps == null || webClient == null) {
            log.error("API provider '{}' not configured or WebClient not initialized.", providerName);
            return Optional.empty();
        }

        String endpoint = providerProps.getResourcePath();
        for (Map.Entry<String, String> entry : resourcePathVariables.entrySet()) {
            endpoint = endpoint.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        StringBuilder fullUrlBuilder = new StringBuilder(endpoint);
        if (queryParams != null && !queryParams.isEmpty()) {
            fullUrlBuilder.append("?");
            queryParams.forEach((key, value) -> {
                if (fullUrlBuilder.charAt(fullUrlBuilder.length() - 1) != '?') {
                    fullUrlBuilder.append("&");
                }
                fullUrlBuilder.append(key).append("=").append(value);
            });
        }
        String fullUrl = fullUrlBuilder.toString();

        log.info("Making API call to provider '{}' at URL: {}", providerName, fullUrl);

        try {
            Mono<T> responseMono = webClient.get()
                    .uri(fullUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(responseType)
                    .retryWhen(Retry.backoff(providerProps.getMaxRetries(), Duration.ofMillis(providerProps.getRetryDelayMs()))
                            .filter(throwable ->
                                    (throwable instanceof WebClientResponseException &&
                                            ((WebClientResponseException) throwable).getStatusCode().is5xxServerError())
                                            || throwable instanceof java.net.ConnectException
                                            || throwable instanceof java.util.concurrent.TimeoutException)
                            .doBeforeRetry(retrySignal ->
                                    log.warn("Retrying API call for provider {} (attempt {}): {}",
                                            providerName, retrySignal.totalRetries() + 1, retrySignal.failure().getMessage()))
                            .onRetryExhaustedThrow((spec, signal) -> {
                                log.error("API call for provider {} exhausted retries after {} attempts. Last error: {}",
                                        providerName, providerProps.getMaxRetries(), signal.failure().getMessage());
                                return signal.failure();
                            })
                    )
                    .doOnError(e -> log.error("Error during WebClient call for provider {}: {}", providerName, e.getMessage(), e));
            //todo: make this fully non blocking
            T response = responseMono.block();
            return Optional.ofNullable(response);

        } catch (WebClientResponseException e) {
            log.error("WebClient error response for provider {}: Status {} - {}", providerName, e.getStatusCode(), e.getResponseBodyAsString());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Generic error during API call for provider {}: {}", providerName, e.getMessage(), e);
            return Optional.empty();
        }
    }

    public <T, R> Optional<T> executePost(String providerName, Map<String, String> resourcePathVariables, R requestBody, Class<T> responseType) {
        ApiProviderProperties providerProps = restConfigProperties.getProviders().get(providerName);
        WebClient webClient = webClients.get(providerName);

        if (providerProps == null || webClient == null) {
            log.error("API provider '{}' not configured or WebClient not initialized.", providerName);
            return Optional.empty();
        }

        String endpoint = providerProps.getResourcePath();
        for (Map.Entry<String, String> entry : resourcePathVariables.entrySet()) {
            endpoint = endpoint.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        String fullUrl = endpoint;

        log.info("Making POST API call to provider '{}' at URL: {}", providerName, fullUrl);

        try {
            Mono<T> responseMono = webClient.post()
                    .uri(fullUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(responseType)
                    .retryWhen(Retry.backoff(providerProps.getMaxRetries(), Duration.ofMillis(providerProps.getRetryDelayMs()))
                            .filter(throwable ->
                                    (throwable instanceof WebClientResponseException &&
                                            ((WebClientResponseException) throwable).getStatusCode().is5xxServerError())
                                            || throwable instanceof java.net.ConnectException
                                            || throwable instanceof java.util.concurrent.TimeoutException)
                            .doBeforeRetry(retrySignal ->
                                    log.warn("Retrying POST API call for provider {} (attempt {}): {}",
                                            providerName, retrySignal.totalRetries() + 1, retrySignal.failure().getMessage()))
                            .onRetryExhaustedThrow((spec, signal) -> {
                                log.error("POST API call for provider {} exhausted retries after {} attempts. Last error: {}",
                                        providerName, providerProps.getMaxRetries(), signal.failure().getMessage());
                                return signal.failure();
                            })
                    )
                    .doOnError(e -> log.error("Error during POST WebClient call for provider {}: {}", providerName, e.getMessage(), e));

            T response = responseMono.block();
            return Optional.ofNullable(response);

        } catch (WebClientResponseException e) {
            log.error("WebClient POST error response for provider {}: Status {} - {}", providerName, e.getStatusCode(), e.getResponseBodyAsString());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Generic error during POST API call for provider {}: {}", providerName, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
