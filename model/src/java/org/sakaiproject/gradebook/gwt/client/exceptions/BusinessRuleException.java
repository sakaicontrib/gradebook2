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

package org.sakaiproject.gradebook.gwt.client.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;

public class BusinessRuleException extends InvalidInputException {

	private static final long serialVersionUID = 1L;
	private List<BusinessLogicCode> codes = new ArrayList<BusinessLogicCode>();

	public List<BusinessLogicCode> getCodes() {
		return codes;
	}

	public BusinessRuleException() {
		super();
	}

	public BusinessRuleException(String message) {
		super(message);
	}

	public BusinessRuleException(String message,
			BusinessLogicCode code) {
		
		super(message);
		if(code != null) {
			codes.add(code);
		}
		
	}

}
