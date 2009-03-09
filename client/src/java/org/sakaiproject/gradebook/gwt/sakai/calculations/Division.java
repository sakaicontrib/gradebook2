package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Division extends Calculation {

	public Division(BigDecimal x, BigDecimal y, Mode mode, PrintWriter writer) {
		super(x, y, mode, writer);
	}

	@Override
	public BigDecimal calculate() {
		if (x == null || y == null)
			return null;
		
		if (x.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		
		if (y.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		
		return x.divide(y, RoundingMode.HALF_EVEN);
	}

	@Override
	public String represent() {
		return new StringBuilder().append(x).append(" / ").append(y).toString();
	}

}
