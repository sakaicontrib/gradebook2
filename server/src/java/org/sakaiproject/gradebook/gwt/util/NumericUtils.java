package org.sakaiproject.gradebook.gwt.util;

import java.math.BigDecimal;

import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;

public class NumericUtils {
	public static double divideWithPrecision(double dend, double dor)
	{
		double ret = 0.0; 
		String send = Double.toString(dend); 
		String sor = Double.toString(dor); 
		BigDecimal val = new BigDecimal(send); 
		BigDecimal divisor = new BigDecimal(sor); 
		BigDecimal result = val.divide(divisor, GradeCalculations.MATH_CONTEXT);
		ret = result.doubleValue(); 
		return ret; 
	}



}
