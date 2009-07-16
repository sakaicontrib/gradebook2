package org.sakaiproject.gradebook.gwt.sakai.model;

import java.math.BigDecimal;

public class GradeStatistics {

	private BigDecimal mean;
	private BigDecimal median;
	private BigDecimal mode;
	private BigDecimal standardDeviation;
	
	public GradeStatistics() {
		
	}

	public BigDecimal getMean() {
		return mean;
	}

	public void setMean(BigDecimal mean) {
		this.mean = mean;
	}

	public BigDecimal getMedian() {
		return median;
	}

	public void setMedian(BigDecimal median) {
		this.median = median;
	}

	public BigDecimal getMode() {
		return mode;
	}

	public void setMode(BigDecimal mode) {
		this.mode = mode;
	}

	public BigDecimal getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(BigDecimal standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	
	
	
}
