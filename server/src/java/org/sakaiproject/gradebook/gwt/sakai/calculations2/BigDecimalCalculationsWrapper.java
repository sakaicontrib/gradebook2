package org.sakaiproject.gradebook.gwt.sakai.calculations2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigDecimalCalculationsWrapper {
	
	public static final int MATHCONTEXT_PRECISON = 50;
	public static final MathContext MATHCONTEXT_50_HALF_EVEN  = new MathContext(MATHCONTEXT_PRECISON, RoundingMode.HALF_EVEN);
	
	public BigDecimal add(BigDecimal addend, BigDecimal augend) {
		
		return addend.add(augend);
	}
	
	public BigDecimal subtract(BigDecimal minuend, BigDecimal subtrahend) {
		
		return minuend.subtract(subtrahend);
	}
	
	public BigDecimal multiply(BigDecimal multiplier, BigDecimal multiplicand) {
		
		return multiplier.multiply(multiplicand);
	}
	
	public BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		
		BigDecimal result = null;
		
		try {
		
			result = dividend.divide(divisor);
		
		} catch(ArithmeticException ae) {
			
			result = dividend.divide(divisor, MATHCONTEXT_50_HALF_EVEN);
		}
		
		return result;
	}
}
