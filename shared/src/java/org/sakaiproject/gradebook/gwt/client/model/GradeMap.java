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
package org.sakaiproject.gradebook.gwt.client.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GradeMap implements IsSerializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String, Double> gradeMapping = new LinkedHashMap<String, Double>();
	private Map<String, Double> lowerBoundsMapping = new LinkedHashMap<String, Double>();
	
	private Double upperBound;
	
	public GradeMap() {
		
	}
	
	public List<String> getValidGradeInputs() {
		return new ArrayList<String>(gradeMapping.keySet());
	}
	
	public void setLowerBound(String grade, Double lowerBound) {
		lowerBoundsMapping.put(grade, lowerBound);
	}
	
	public void setLetterGradePoints(String grade, Double points) {
		gradeMapping.put(grade, points);
	}
	
	public Iterator<String> getLetterGradeKeyIterator() {
		return gradeMapping.keySet().iterator();
	}
	
	public Iterator<String> getLowerBoundKeyIterator() {
		return lowerBoundsMapping.keySet().iterator();
	}
	
	public Double getLetterGradePoints(String key) {
		return gradeMapping.get(key);
	}
	
	public Double getLowerBound(String key) {
		return lowerBoundsMapping.get(key);
	}
	
	public Double determinePoints(String letterGrade, Double maxPoints) {
		Double percentagePoints = getLetterGradePoints(letterGrade);
		
		double p = percentagePoints.doubleValue() / 100.0;
		
		double points = maxPoints.doubleValue() * p;
		
		return points;
	}
	
	public String calculate(Double value) {
		
		for (String key : lowerBoundsMapping.keySet()) {
			Double cutOff = lowerBoundsMapping.get(key);
			
			if (value.compareTo(cutOff) >= 0) {
				return key;
			}
		}
		
		return "N/A";
	}
	
	public Double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(Double upperBound) {
		this.upperBound = upperBound;
	}
	
	
}
