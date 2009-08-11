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

import com.google.gwt.user.client.rpc.IsSerializable;

public class GradebookStatistics implements IsSerializable {

	private static final long serialVersionUID = 1L;

	private String numberOfStudents;
	private String numberOfGradeableItems;
	private String high;
	private String low;
	private String mean;
	private String median;
	private String meanStandardDeviation;

	public GradebookStatistics() {} 
	
	public GradebookStatistics(String numberOfStudents,
			String numberOfGradeableItems, String high, String low, String mean,
			String median, String meanStandardDeviation) {
		super();
		this.numberOfStudents = numberOfStudents;
		this.numberOfGradeableItems = numberOfGradeableItems;
		this.high = high;
		this.low = low;
		this.mean = mean;
		this.median = median;
		this.meanStandardDeviation = meanStandardDeviation;
	}

	public String getNumberOfStudents() {
		return numberOfStudents;
	}

	public void setNumberOfStudents(String numberOfStudents) {
		this.numberOfStudents = numberOfStudents;
	}

	public String getNumberOfGradeableItems() {
		return numberOfGradeableItems;
	}

	public void setNumberOfGradeableItems(String numberOfGradeableItems) {
		this.numberOfGradeableItems = numberOfGradeableItems;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getMean() {
		return mean;
	}

	public void setMean(String mean) {
		this.mean = mean;
	}

	public String getMedian() {
		return median;
	}

	public void setMedian(String median) {
		this.median = median;
	}

	public String getMeanStandardDeviation() {
		return meanStandardDeviation;
	}

	public void setMeanStandardDeviation(String meanStandardDeviation) {
		this.meanStandardDeviation = meanStandardDeviation;
	}

}
