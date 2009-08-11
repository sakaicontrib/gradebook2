/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

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
