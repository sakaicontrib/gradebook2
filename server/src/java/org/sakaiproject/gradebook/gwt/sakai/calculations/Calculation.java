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
