package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.PrintWriter;
import java.math.BigDecimal;

public class Subtraction extends Calculation {

	public Subtraction(BigDecimal x, BigDecimal y, Mode mode, PrintWriter writer) {
		super(x, y, mode, writer);
	}

	@Override
	public BigDecimal calculate() {
		if (x == null || y == null)
			return null;
		
		return x.subtract(y);
	}

	@Override
	public String represent() {
		return new StringBuilder().append(x).append(" - ").append(y).toString();
	}

}
