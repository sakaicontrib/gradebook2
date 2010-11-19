package org.sakaiproject.gradebook.gwt.sakai.calculations2;

import java.math.BigDecimal;

public interface GradeRecordCalculationUnit {
	
	public BigDecimal calculate(BigDecimal weight);

	public BigDecimal calculateEqually(int numItems);

	public void calculatePercentageScore();
	
	public void calculateRawDifference();
	
	public BigDecimal getPercentOfCategory();

	public BigDecimal getScaledScore();

	public boolean isExcused();

	public boolean isExtraCredit();

	public BigDecimal getPointsReceived();

	public BigDecimal getPointsPossible();

	public BigDecimal getPercentageScore();

	public void setExcused(boolean isExcused);

	public boolean isDropped();

	public void setDropped(boolean isDropped);

	public Object getActualRecord();

	public void setActualRecord(Object actualRecord);

	public BigDecimal getPointsDifference();

}
