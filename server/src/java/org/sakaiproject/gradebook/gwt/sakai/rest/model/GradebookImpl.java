package org.sakaiproject.gradebook.gwt.sakai.rest.model;

import java.util.HashMap;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Statistics;
import org.sakaiproject.gradebook.gwt.client.model.key.GradebookKey;
import org.sakaiproject.gradebook.gwt.sakai.Util;

public class GradebookImpl extends HashMap<String, Object> implements Gradebook {

	private static final long serialVersionUID = 1L;

	public GradebookImpl() {
		super();
		setNewGradebook(Boolean.FALSE);
	}
	
	public <X> X get(String property) {
		return (X)super.get(property);
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

	public <X> X set(String property, X value) {
		return (X)put(property, value);
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

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getIdentifier()
	 */
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
		return Util.toBoolean(get(GradebookKey.ISNEWGRADEBOOK.name()));
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
