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

import java.util.List;


import com.google.gwt.user.client.rpc.IsSerializable;

public class GradebookModel extends EntityModel implements IsSerializable, Gradebook {

	private static final long serialVersionUID = 1L;
	
	public GradebookModel() {
		setNewGradebook(Boolean.FALSE);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getGradebookUid()
	 */
	public String getGradebookUid() {
		return get(GradebookKey.GRADEBOOKUID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setGradebookUid(java.lang.String)
	 */
	public void setGradebookUid(String gradebookUid) {
		set(GradebookKey.GRADEBOOKUID.name(), gradebookUid);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getGradebookId()
	 */
	public Long getGradebookId() {
		return get(GradebookKey.GRADEBOOKID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setGradebookId(java.lang.Long)
	 */
	public void setGradebookId(Long gradebookId) {
		set(GradebookKey.GRADEBOOKID.name(), gradebookId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getConfigurationModel()
	 */
	public Configuration getConfigurationModel() {
		return get(GradebookKey.CONFIGURATIONMODEL.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setConfigurationModel(org.sakaiproject.gradebook.gwt.client.model.Configuration)
	 */
	public void setConfigurationModel(Configuration configuration) {
		set(GradebookKey.CONFIGURATIONMODEL.name(), configuration);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getName()
	 */
	public String getName() {
		return get(GradebookKey.NAME.name());
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setName(java.lang.String)
	 */
	public void setName(String name) {
		set(GradebookKey.NAME.name(), name);
	}	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getColumns()
	 */
	public List<FixedColumn> getColumns() {
		return get(GradebookKey.COLUMNS.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setColumns(java.util.List)
	 */
	public void setColumns(List<FixedColumn> columns) {
		set(GradebookKey.COLUMNS.name(), columns);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getUserAsStudent()
	 */
	public Learner getUserAsStudent() {
		return get(GradebookKey.USERASSTUDENT.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setUserAsStudent(org.sakaiproject.gradebook.gwt.client.model.Learner)
	 */
	public void setUserAsStudent(Learner userAsStudent) {
		set(GradebookKey.USERASSTUDENT.name(), userAsStudent);
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getGradebookUid();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getUserName()
	 */
	public String getUserName() {
		return get(GradebookKey.USERNAME.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setUserName(java.lang.String)
	 */
	public void setUserName(String userName) {
		set(GradebookKey.USERNAME.name(), userName);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getGradebookItemModel()
	 */
	public Item getGradebookItemModel() {	
		return get(GradebookKey.GRADEBOOKITEMMODEL.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setGradebookGradeItem(org.sakaiproject.gradebook.gwt.client.model.Item)
	 */
	public void setGradebookGradeItem(Item gradebookGradeItem) {
		set(GradebookKey.GRADEBOOKITEMMODEL.name(), gradebookGradeItem);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#isNewGradebook()
	 */
	public Boolean isNewGradebook() {
		return get(GradebookKey.ISNEWGRADEBOOK.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setNewGradebook(java.lang.Boolean)
	 */
	public void setNewGradebook(Boolean isNewGradebook) {
		set(GradebookKey.ISNEWGRADEBOOK.name(), isNewGradebook);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getStatsModel()
	 */
	public List<Statistics> getStatsModel() {
		return get(GradebookKey.STATSMODELS.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setStatsModel(java.util.List)
	 */
	public void setStatsModel(List<Statistics> statsModel) {
		set(GradebookKey.STATSMODELS.name(), statsModel);
	}
}
