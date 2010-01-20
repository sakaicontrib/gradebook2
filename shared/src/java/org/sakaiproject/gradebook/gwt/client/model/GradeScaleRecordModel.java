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

	public GradeScaleRecordModel() { 
		super();
	}
	
	public GradeScaleRecordModel(String letterGrade, Double fromRange, Double toRange) {
		set(GradeMapKey.LETTER_GRADE.name(), letterGrade);
		set(GradeMapKey.FROM_RANGE.name(), fromRange);
		set(GradeMapKey.TO_RANGE.name(), toRange);		
	}
	
	@Override
	public String getIdentifier() {
		return get(GradeMapKey.ID.name());
	}
	
	public void setIdentifier(String letterGrade) {
		set(GradeMapKey.ID.name(), letterGrade);
	}
	
	@Override
	public String getDisplayName() {
		return getLetterGrade();
	}
	
	public String getLetterGrade() {
		return get(GradeMapKey.LETTER_GRADE.name());
	}
	
	public Double getFromRange() {
		return get(GradeMapKey.FROM_RANGE.name());
	}
	
	public Double getToRange() {
		return get(GradeMapKey.TO_RANGE.name());
	}
	
	public void setLetterGrade(String letterGrade) {
		set(GradeMapKey.LETTER_GRADE.name(), letterGrade);
	}
	
	public void setFromRange(Double fromRange) {
		set(GradeMapKey.FROM_RANGE.name(), fromRange);
	}
	
	public void setToRange(Double toRange) {
		set(GradeMapKey.TO_RANGE.name(), toRange);
	}
	
	public GradeMapKey getKey(String keyName) {
		
		return GradeMapKey.valueOf(keyName);
	}
}
