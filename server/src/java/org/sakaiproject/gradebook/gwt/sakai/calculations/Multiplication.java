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
