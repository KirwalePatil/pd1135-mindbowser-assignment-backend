
package com.mindbowser.ksp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HuggingFaceSummaryResponse {

	@JsonProperty("summary_text")
	private String summaryText;

	public String getSummaryText() {
		return summaryText;
	}

	
	
	public void setSummaryText(String summaryText) {
		this.summaryText = summaryText;
	}
}
