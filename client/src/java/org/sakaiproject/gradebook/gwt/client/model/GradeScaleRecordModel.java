/**********************************************************************************
*
* $Id: GradeScaleRecordModel.java 6638 2009-01-22 01:27:23Z jrenfro $
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
package org.sakaiproject.gradebook.gwt.client.model;

public class GradeScaleRecordModel extends EntityModel {

	private static final long serialVersionUID = 1L;

	// NOTE: the letterGrade is used for both ID and LETTER_GRADE properties
	// so that we don't store the data twice
	public enum Key { ID, LETTER_GRADE, FROM_RANGE, TO_RANGE }
	
	public GradeScaleRecordModel() { 
		super();
	}
	
	public GradeScaleRecordModel(String letterGrade, Double fromRange, Double toRange) {
		set(Key.LETTER_GRADE.name(), letterGrade);
		set(Key.FROM_RANGE.name(), fromRange);
		set(Key.TO_RANGE.name(), toRange);		
	}
	
	@Override
	public String getIdentifier() {
		return get(Key.LETTER_GRADE.name());
	}
	
	public void setIdentifier(String letterGrade) {
		set(Key.ID.name(), letterGrade);
	}
	
	@Override
	public String getDisplayName() {
		return getLetterGrade();
	}
	
	public String getLetterGrade() {
		return get(Key.LETTER_GRADE.name());
	}
	
	public Double getFromRange() {
		return get(Key.FROM_RANGE.name());
	}
	
	public Double getToRange() {
		return get(Key.TO_RANGE.name());
	}
	
	public void setLetterGrade(String letterGrade) {
		set(Key.LETTER_GRADE.name(), letterGrade);
	}
	
	public void setFromRange(Double fromRange) {
		set(Key.FROM_RANGE.name(), fromRange);
	}
	
	public void setToRange(Double toRange) {
		set(Key.TO_RANGE.name(), toRange);
	}
	
	public Key getKey(String keyName) {
		
		return Key.valueOf(keyName);
	}
}
