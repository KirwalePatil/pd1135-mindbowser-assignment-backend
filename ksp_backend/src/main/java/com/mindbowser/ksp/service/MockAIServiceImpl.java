package com.mindbowser.ksp.service;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class MockAIServiceImpl implements AIService {

	@Override
	public Mono<String> generateSummary(String content) {
		// For mock, just return the original content with " [AI Improved]" appended
		return Mono.just(content + " [AI Summarised]");
	}
	

	
	public Mono<String> improveContent(String content) {
		// Add mock improvements: fix grammar, make concise, etc.
		return Mono.just(content.replaceAll("\\s+", " ").trim() + " [Content Improved by AI]");
	}
}