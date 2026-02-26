package com.mindbowser.ksp.service;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.mindbowser.ksp.dto.HuggingFaceSummaryResponse;

import reactor.core.publisher.Mono;

@Service
public class HuggingFaceAIServiceImpl implements AIService {

    
    private final WebClient webClient;

    public HuggingFaceAIServiceImpl(
            @Value("${hf.api.url}") String apiUrl,
            @Value("${hf.api.token}") String token) {

        System.out.println("HuggingFaceAIServiceImpl loaded");

        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public Mono<String> generateSummary(String content) {

        if (content == null || content.isEmpty()) {
            return Mono.just("");
        }

        return webClient.post()
                .bodyValue(Map.of("inputs", content))
                .retrieve()
                .bodyToMono(HuggingFaceSummaryResponse[].class)
                .timeout(Duration.ofSeconds(10)) // prevent hanging forever
                .map(res -> {
                    String summary = res.length > 0 ? res[0].getSummaryText() : "";

                    summary = summary.replaceAll("http[s]?://\\S+", "");
                    summary = summary.replaceAll("www\\.\\S+", "");

                    if (summary.length() > 500) {
                        summary = summary.substring(0, 500);
                    }

                    return summary.trim();
                })
                .onErrorResume(ex -> {
                    System.err.println("HuggingFace API failed: " + ex.getMessage());

                    // Safe fallback summary
                    return Mono.just(generateFallbackSummary(content));
                });
    }

    /**
     * Simple local fallback summarizer
     * Returns first 2-3 sentences or first 300 chars
     */
    private String generateFallbackSummary(String content) {
        String cleaned = content.replaceAll("http[s]?://\\S+", "")
                                .replaceAll("www\\.\\S+", "")
                                .trim();

        if (cleaned.length() <= 300) {
            return cleaned;
        }

        return cleaned.substring(0, 300) + "...";
    }
}