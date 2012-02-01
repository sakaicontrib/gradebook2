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

import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.type.ClassType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;

public class DataTypeConversionUtil {

	public static boolean checkBoolean(Boolean b) {
		return b != null && b.booleanValue();
	}
	
	public static String concat(String... vars) {
		StringBuilder builder = new StringBuilder();
		
		for (int i=0;i<vars.length;i++) {
			builder.append(vars[i]);
		}
		
		return builder.toString();
	}
	
	public static NumberFormat getDefaultNumberFormat() {
		return NumberFormat.getFormat("#.#####");
	}
	
	public static NumberFormat getShortNumberFormat() {
		return NumberFormat.getFormat("#.##");
	}
	
	public static NumberFormat getLongNumberFormat() {
		return NumberFormat.getFormat("#.#####");
	}
	
	public static String convertDateToString(Date d) {
		return DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT).format(d);
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
	
	public static String buildCommentKey(String itemId) {
		return new StringBuilder(AppConstants.COMMENTED_FLAG).append(itemId).toString();
	}
	
	public static String buildCommentTextKey(String itemId) {
		return new StringBuilder(AppConstants.COMMENT_TEXT_FLAG).append(itemId).toString();
	}
	
	public static String buildDroppedKey(String itemId) {
		return new StringBuilder(AppConstants.DROP_FLAG).append(itemId).toString();
	}
	
	public static String buildExcusedKey(String itemId) {
		return new StringBuilder(AppConstants.EXCUSE_FLAG).append(itemId).toString();
	}
	
	public static String buildFailedKey(String itemId) {
		return new StringBuilder(AppConstants.FAILED_FLAG).append(itemId).toString();
	}
	
	public static String buildSuccessKey(String itemId) {
		return new StringBuilder(AppConstants.SUCCESS_FLAG).append(itemId).toString();
	}
	
	// GRBK-668
	public static String buildConvertedMessageKey(String itemId) {
		return new StringBuilder(AppConstants.CONVERTED_FLAG).append(itemId).toString();
	}
	
	//TODO: this is really brittle... convert to two args, one being the flag constant 
	// 		Or use a delimiter like the colon (':')
	public static String unpackItemIdFromKey(String key) {
		if (key == null || key.length() < 5)
			return null;
		return key.substring(4);
	}

	public static ClassType lookupClassType(String property, GradeType gradeType) {
		
		if (property.equals(LearnerKey.S_OVRD_GRD.name()))
			return ClassType.STRING;
		
		if (property.startsWith(AppConstants.COMMENT_TEXT_FLAG))
			return ClassType.STRING;
		
		if (property.startsWith(AppConstants.EXCUSE_FLAG))
			return ClassType.BOOLEAN;
		
		if (gradeType == GradeType.LETTERS)
			return ClassType.STRING;
		
		return ClassType.DOUBLE;
	}
}
