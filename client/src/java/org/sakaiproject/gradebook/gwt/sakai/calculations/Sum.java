package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Sum extends Calculation {

	private List<BigDecimal> items;
	private BigDecimal sum;
	
	public Sum(Mode mode, PrintWriter writer) {
		super(mode, writer);
		this.sum = null;
		this.items = new ArrayList<BigDecimal>();
	}
	
	public void add(BigDecimal item) {
		if (item != null) {
			items.add(item);
			// We only want sum to be non-null if there is at least one non-null item
			if (sum == null)
				sum = BigDecimal.ZERO;
			sum = sum.add(item);
		}
	}

	@Override
	public BigDecimal calculate() {
		return sum;
	}

	@Override
	public String represent() {
		StringBuilder builder = new StringBuilder();
		builder.append("SUM[");
		for (BigDecimal item : items) {
			builder.append(item).append(" ");
		}
		builder.append("] = ").append(sum);
		return builder.toString();
	}

}
