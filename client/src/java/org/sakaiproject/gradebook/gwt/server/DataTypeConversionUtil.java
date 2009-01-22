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
package org.sakaiproject.gradebook.gwt.server;

import java.text.NumberFormat;

public class DataTypeConversionUtil {

	public static Double convertStringToDouble(String s) {
		if (s == null)
			return null;
		
		return Double.valueOf(s);
	}
	
	public static String formatDoubleAsPercentString(Double d) {
		if (d == null)
			return null;
		
		return NumberFormat.getPercentInstance().format(d);
	}
	
	public static String formatDoubleAsPointsString(Double d) {
		if (d == null)
			return "";
		return NumberFormat.getInstance().format(d);
	}
	
	public static String formatDoubleAsPointsString(String s) {
		if (s == null)
			return null;
		
		return formatDoubleAsPointsString(convertStringToDouble(s));
	}
	
	
}
