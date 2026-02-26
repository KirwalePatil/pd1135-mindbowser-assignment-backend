package com.mindbowser.ksp.service;

import reactor.core.publisher.Mono;

public interface AIService {
	
	Mono<String> generateSummary(String content);
}
