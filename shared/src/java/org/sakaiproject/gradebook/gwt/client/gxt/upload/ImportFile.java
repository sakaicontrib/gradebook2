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

package org.sakaiproject.gradebook.gwt.client.gxt.upload;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImportFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<ImportHeader> items;

	private ArrayList<ImportRow> rows;

	private Boolean hasCategories;
	
	private Boolean hasWeights;
	
	private Boolean isLetterGrading;
	
	private Boolean isPointsMode;

	private String notes; 

	private boolean hasErrors; 

	private boolean notifyAssignmentName; 

	public ImportFile() {
		hasErrors = false; 
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}


	public boolean isHasErrors() {
		return hasErrors;
	}

	public void setHasErrors(boolean hasErrors) {
		this.hasErrors = hasErrors;
	}

	public List<ImportRow> getRows() {
		return rows;
	}

	public void setRows(ArrayList<ImportRow> rows) {
		this.rows = rows;
	}

	public ArrayList<ImportHeader> getItems() {
		return items;
	}

	public void setItems(ArrayList<ImportHeader> items) {
		this.items = items;
	}

	public Boolean getHasCategories() {
		return hasCategories;
	}

	public void setHasCategories(Boolean hasCategories) {
		this.hasCategories = hasCategories;
	}

	public boolean isNotifyAssignmentName() {
		return notifyAssignmentName;
	}

	public void setNotifyAssignmentName(boolean notifyAssignmentName) {
		this.notifyAssignmentName = notifyAssignmentName;
	}

	public Boolean isLetterGrading() {
		return isLetterGrading;
	}

	public void setLetterGrading(Boolean isLetterGrading) {
		this.isLetterGrading = isLetterGrading;
	}

	public Boolean getHasWeights() {
		return hasWeights;
	}

	public void setHasWeights(Boolean hasWeights) {
		this.hasWeights = hasWeights;
	}

	public Boolean isPointsMode() {
		return isPointsMode;
	}

	public void setPointsMode(Boolean isPointsMode) {
		this.isPointsMode = isPointsMode;
	}

}
