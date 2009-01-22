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
package org.sakaiproject.gradebook.gwt.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

public class DataTypeConversionUtil {

	private static DateTimeFormat dateFormat = DateTimeFormat.getShortDateFormat();

	public static NumberFormat getDefaultNumberFormat() {
		return NumberFormat.getFormat("#.#####");
	}
	
	public static String convertDateToString(Date d) {
		return dateFormat.format(d);
	}
	
	public static Integer convertStringToInteger(String s) {
		return Integer.valueOf(s);
	}
	
	public static Double convertStringToDouble(String s) {
		return Double.valueOf(s);
	}
	
	public static String formatDoubleAsPercentString(Double d) {
		if (d == null)
			return "0.0";
		
		return NumberFormat.getDecimalFormat().format(d);
	}
	
	public static String formatDoubleAsPointsString(Double d) {
		if (d == null)
			return "0.0";
		
		return NumberFormat.getDecimalFormat().format(d);
	}
	
	public static String formatDoubleAsPercentString(String s) {
		return formatDoubleAsPercentString(convertStringToDouble(s));
	}
	
	public static String formatDoubleAsPointsString(String s) {
		return formatDoubleAsPointsString(convertStringToDouble(s));
	}
	
	
}
