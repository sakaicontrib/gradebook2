package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.PrintWriter;
import java.math.BigDecimal;

public class Multiplication extends Calculation {

	public Multiplication(BigDecimal x, BigDecimal y, Mode mode, PrintWriter writer) {
		super(x, y, mode, writer);
	}

	@Override
	public BigDecimal calculate() {
		if (x == null || y == null)
			return null;
		
		return x.multiply(y);
	}

	@Override
	public String represent() {
		return new StringBuilder().append(x).append(" x ").append(y).toString();
	}

}
