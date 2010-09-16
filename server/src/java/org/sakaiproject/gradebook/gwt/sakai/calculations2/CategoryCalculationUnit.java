package org.sakaiproject.gradebook.gwt.sakai.calculations2;

import java.math.BigDecimal;
import java.util.List;

public interface CategoryCalculationUnit {

	public BigDecimal calculate(List<GradeRecordCalculationUnit> units, boolean isExtraCreditScaled);
	
	public boolean isExtraCredit();

	public BigDecimal getCategoryWeightTotal();

	public BigDecimal getCategoryGrade();

	public int getDropLowest();

	public void setDropLowest(int dropLowest);

	public boolean isPointsWeighted();

	public void setPointsWeighted(boolean isPointsWeighted);

	public void setCategoryGrade(BigDecimal categoryGrade);

	public BigDecimal getTotalCategoryPoints();

	public void setTotalCategoryPoints(BigDecimal totalCategoryPoints);
}
