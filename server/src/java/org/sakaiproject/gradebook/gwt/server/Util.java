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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.sakaiproject.util.ResourceLoader;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

public class Util {
	
	// Set via IoC
	private static ResourceLoader i18n;
	
	public static Double fromPercentString(String s) throws NumberFormatException {
		if (s != null) {
			s = s.replace("%", "");
			double p = Double.parseDouble(s);
			return Double.valueOf(p);
		}
		return null;
	}
	
	public static Boolean toBoolean(Object object) {
		Boolean b = null;
		
		if (object != null) {
			if (object instanceof Boolean)
				b = (Boolean)object;
		}
		
		return b;
	}
	
	public static boolean toBooleanPrimitive(Object object) {
		assert(object instanceof Boolean);
		
		boolean b = Util.checkBoolean((Boolean)object);
		return b;
	}
	
	public static CategoryType toCategoryType(Object object) {
		CategoryType t = null;
		
		if (object != null) {
			if (object instanceof CategoryType)
				t = (CategoryType)object;
			else if (object instanceof String)
				t = CategoryType.valueOf((String)object);
		}
		
		return t;
	}
	
	public static Date toDate(Object object) throws InvalidInputException {
		Date d = null;
		
		if (object != null) {
			if (object instanceof Date)
				d = (Date)object;
			else if (object instanceof Long)
				d = new Date((Long)object);
			else if (object instanceof String) { 
				
				// GRBK-673 : We receive a date as a string from the client. We apply the same
				// format as the client side does. Also the client handles two types of date formats
				// and we do the same thing here. See: EntityOverlay.java safeGet() {...}
				
				try {
					
					DateFormat longDateFormat = new SimpleDateFormat(AppConstants.LONG_DATE);
					d = longDateFormat.parse((String) object);
				}
				catch (ParseException e) {
					
					try {
						
						DateFormat shortDateFormat = new SimpleDateFormat(AppConstants.SHORT_DATE);
						d = shortDateFormat.parse((String) object);
					}
					catch (ParseException e1) {
						
						// GRBK-961
						try {
							
							DateFormat inputDateFormat = new SimpleDateFormat(AppConstants.INPUT_DATE);
							d = inputDateFormat.parse((String) object);
						}
						catch (ParseException e2) {
							
							throw new InvalidInputException(i18n.getString("invalidDateObjectError"));
						}
					}
				}
			}
		}
		
		return d;
	}
	
	public static Double toDouble(Object object) {
		assert(object instanceof Number);
		
		Double d = null;
		if (object instanceof Integer)
			d = object == null ? null : Double.valueOf(((Integer)object).doubleValue());
		else if (object instanceof Long)
			d = object == null ? null : Double.valueOf(((Long)object).doubleValue());
		else if (object instanceof Double)
			d = (Double)object;
		
		return d;
	}
	
	public static double toDouble(Map<String, Object> map, String property) {
		double d = 0d;
		
		if (map != null) {
			Double value = (Double)map.get(property);
			if (value != null)
				d = value.doubleValue();
		}
	
		return d;
	}
	
	public static GradeType toGradeType(Object object) {
		GradeType t = null;
		
		if (object != null) {
			if (object instanceof GradeType)
				t = (GradeType)object;
			else if (object instanceof String)
				t = GradeType.valueOf((String)object);
		}
		
		return t;
	}
	
	public static Integer toInteger(Object object) {
		Integer l = null;

		if (object != null) {
			if (object instanceof Integer)
				l = (Integer)object;
			else if (object instanceof String)
				l = Integer.valueOf((String)object);
		}
		
		return l;
	}
	
	public static ItemType toItemType(Object object) {
		ItemType t = null;
		
		if (object != null) {
			if (object instanceof ItemType)
				t = (ItemType)object;
			else if (object instanceof String)
				t = ItemType.valueOf((String)object);
		}
		
		return t;
	}
	
	public static Long toLong(Object object) {
		Long l = null;

		if (object != null) {
			if (object instanceof Integer)
				l = Long.valueOf(((Integer)object).longValue());
			else if (object instanceof Long)
				l = (Long)object;
			else if (object instanceof String)
				l = Long.valueOf((String)object);
		}
		
		return l;
	}
	
	public static String toString(Object object) {
		String s = null;
		
		if (object != null) {
			if (object instanceof String)
				s = (String)object;
			else
				s = String.valueOf(object);
		}
		
		return s;
	}
	
	public static boolean notEquals(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return false;

		return ( 
				(o1 == null && o2 != null)
				|| (o1 != null && o2 == null)
				|| (!o1.equals(o2))
		);
	}

	public static boolean isNotNullOrEmpty(String s) {
		return s != null && s.trim().length() > 0;
	}

	public static boolean checkBoolean(Boolean b) {
		return b != null && b.booleanValue();
	}

	public static Double convertStringToDouble(String s) throws NumberFormatException {
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
	
	public static String buildConvertedGradeKey(String itemId) {
		return new StringBuilder(AppConstants.CONVERTED_GRADE).append(itemId).toString();
	}
	
	public static String unpackItemIdFromKey(String key) {
		if (key == null || key.length() < 5)
			return null;
		return key.substring(4);
	}
	
	public static boolean isNumeric(String value) {
		
		if (value.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
			return true;
		} else {
			return false;
		}
	}

}
