package org.sakaiproject.gradebook.gwt.sakai;

import java.math.BigDecimal;

public class DisplayGrade {

	private static final String missingGradesMarker = " ***";
	
	private String letterGrade;
	private BigDecimal calculatedGrade;
	private boolean isOverridden;
	private boolean isLetterGradeMode;
	private boolean isMissingGrades;
	
	public DisplayGrade() {
		this.isOverridden = false;
		this.isMissingGrades = false;
	}
	
	public DisplayGrade(String letterGrade, BigDecimal calculatedGrade, boolean isLetterGradeMode) {
		this();
		this.letterGrade = letterGrade;
		this.calculatedGrade = calculatedGrade;
		this.isLetterGradeMode = isLetterGradeMode;
	}

	public String getLetterGrade() {
		return letterGrade;
	}

	public void setLetterGrade(String letterGrade) {
		this.letterGrade = letterGrade;
	}

	public BigDecimal getCalculatedGrade() {
		return calculatedGrade;
	}

	public void setCalculatedGrade(BigDecimal calculatedGrade) {
		this.calculatedGrade = calculatedGrade;
	}

	public boolean isOverridden() {
		return isOverridden;
	}

	public void setOverridden(boolean isOverridden) {
		this.isOverridden = isOverridden;
	}

	public boolean isMissingGrades() {
		return isMissingGrades;
	}

	public void setMissingGrades(boolean isMissingGrades) {
		this.isMissingGrades = isMissingGrades;
	}
	
	public boolean isLetterGradeMode() {
		return isLetterGradeMode;
	}

	public void setLetterGradeMode(boolean isLetterGradeMode) {
		this.isLetterGradeMode = isLetterGradeMode;
	}
	
	public String getCalculatedGradeAsString() {
		String calculatedGradeAsString = null;
		if (calculatedGrade != null)
			calculatedGradeAsString = calculatedGrade.toString();
		
		return calculatedGradeAsString;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (letterGrade != null) {
			builder.append(letterGrade);
			
			if (isOverridden) {
				builder.append(" (override)");
			} else if (calculatedGrade != null && !isLetterGradeMode) {	
				builder.append(" (").append(calculatedGrade.toString()).append("%)");
			} 
			
			if (isMissingGrades)
				builder.append(missingGradesMarker);
		
			return builder.toString();
		}
		
		return null;
	}
	
}
