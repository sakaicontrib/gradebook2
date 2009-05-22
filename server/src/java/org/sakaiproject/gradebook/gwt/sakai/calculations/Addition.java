package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.PrintWriter;
import java.math.BigDecimal;

public class Addition extends Calculation {

	public Addition(BigDecimal x, BigDecimal y, Mode mode, PrintWriter writer) {
		super(x, y, mode, writer);
	}

	@Override
	public BigDecimal calculate() {
		return x.add(y);
	}

	@Override
	public String represent() {
		return new StringBuilder().append(x).append(" + ").append(y).toString();
	}

}
