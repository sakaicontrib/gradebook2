package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.PrintWriter;
import java.math.BigDecimal;

public abstract class Calculation {

	public enum Mode { DO, SHOW };
	
	protected BigDecimal x;
	protected BigDecimal y;
	protected Mode mode;
	protected PrintWriter writer;
	
	public Calculation(Mode mode, PrintWriter writer) {
		this.mode = mode;
		this.writer = writer;
	}
	
	public Calculation(BigDecimal x, BigDecimal y, Mode mode, PrintWriter writer) {
		this.x = x;
		this.y = y;
		this.mode = mode;
		this.writer = writer;
	}
	
	public abstract BigDecimal calculate();
	
	public abstract String represent();
	
	public BigDecimal perform() {
	
		switch (mode) {
		case SHOW:
			writer.println(represent());
			break;
		}
		
		return calculate();
	}
	
	
}
