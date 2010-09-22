package org.sakaiproject.gradebook.gwt.sakai.calculations2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BigDecimalCalculationsWrapper {
	
	private static final Log log = LogFactory.getLog(BigDecimalCalculationsWrapper.class);
	
	private int precision = 0;
	private MathContext mathContextHalfEven = null;
	
	public void init() {
		System.out.println("XXXX: BigDecimalCalculationsWrapperImpl init");
	}
	
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
			
			/*
			 * We are not handling divide by zero case here because the following call to divide
			 * will generate the exception again. The calling code should handle that case.
			 */
			
			result = dividend.divide(divisor, mathContextHalfEven);
		}
		
		return result;
	}

	
	public void setPrecision(int precision) {

		this.precision = precision;
		
		if(null == mathContextHalfEven) {
		
			log.info("Setting MathContext precision to " + precision);
			mathContextHalfEven  = new MathContext(precision, RoundingMode.HALF_EVEN);
		}
	}
}
