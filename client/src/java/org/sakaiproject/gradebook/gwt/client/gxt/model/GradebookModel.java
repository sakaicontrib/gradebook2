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
package org.sakaiproject.gradebook.gwt.client.gxt.model;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Statistics;
import org.sakaiproject.gradebook.gwt.client.model.key.GradebookKey;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

public class GradebookModel extends EntityModel implements IsSerializable, Gradebook {


	private static final long serialVersionUID = 1L;
	
	public GradebookModel() {
		super();
		setNewGradebook(Boolean.FALSE);
	}
	
	public GradebookModel(EntityOverlay overlay) {
		super(overlay);
		setNewGradebook(Boolean.FALSE);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getGradebookUid()
	 */
	public String getGradebookUid() {
		return get(GradebookKey.S_GB_UID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setGradebookUid(java.lang.String)
	 */
	public void setGradebookUid(String gradebookUid) {
		set(GradebookKey.S_GB_UID.name(), gradebookUid);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getGradebookId()
	 */
	public Long getGradebookId() {
		return getLong(GradebookKey.L_GB_ID.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setGradebookId(java.lang.Long)
	 */
	public void setGradebookId(Long gradebookId) {
		set(GradebookKey.L_GB_ID.name(), gradebookId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getConfigurationModel()
	 */
	public Configuration getConfigurationModel() {
		return get(GradebookKey.M_CONF.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setConfigurationModel(org.sakaiproject.gradebook.gwt.client.model.Configuration)
	 */
	public void setConfigurationModel(Configuration configuration) {
		set(GradebookKey.M_CONF.name(), configuration);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getName()
	 */
	public String getName() {
		return get(GradebookKey.S_NM.name());
	}


	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setName(java.lang.String)
	 */
	public void setName(String name) {
		set(GradebookKey.S_NM.name(), name);
	}	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getColumns()
	 */
	public List<FixedColumn> getColumns() {
		return get(GradebookKey.A_CLMNS.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setColumns(java.util.List)
	 */
	public void setColumns(List<FixedColumn> columns) {
		set(GradebookKey.A_CLMNS.name(), columns);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getUserAsStudent()
	 */
	public Learner getUserAsStudent() {
		return get(GradebookKey.M_USER.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setUserAsStudent(org.sakaiproject.gradebook.gwt.client.model.Learner)
	 */
	public void setUserAsStudent(Learner userAsStudent) {
		set(GradebookKey.M_USER.name(), userAsStudent);
	}

	/*@Override
	public String getDisplayName() {
		return getName();
	}*/

	public String getIdentifier() {
		return getGradebookUid();
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getUserName()
	 */
	public String getUserName() {
		return get(GradebookKey.S_USR_NM.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setUserName(java.lang.String)
	 */
	public void setUserName(String userName) {
		set(GradebookKey.S_USR_NM.name(), userName);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getGradebookItemModel()
	 */
	public Item getGradebookItemModel() {	
		return get(GradebookKey.M_GB_ITM.name());
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setGradebookGradeItem(org.sakaiproject.gradebook.gwt.client.model.Item)
	 */
	public void setGradebookGradeItem(Item gradebookGradeItem) {
		set(GradebookKey.M_GB_ITM.name(), gradebookGradeItem);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#isNewGradebook()
	 */
	public Boolean isNewGradebook() {
		return get(GradebookKey.B_NEW_GB.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setNewGradebook(java.lang.Boolean)
	 */
	public void setNewGradebook(Boolean isNewGradebook) {
		set(GradebookKey.B_NEW_GB.name(), isNewGradebook);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getStatsModel()
	 */
	public List<Statistics> getStatsModel() {
		return get(GradebookKey.A_STATS.name());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#setStatsModel(java.util.List)
	 */
	public void setStatsModel(List<Statistics> statsModel) {
		set(GradebookKey.A_STATS.name(), statsModel);
	}
	
	
	public ModelData newChildModel(String property, EntityOverlay overlay) {
		if (property.equals(GradebookKey.A_STATS.name()))
			return new StatisticsModel(overlay);
		if (property.equals(GradebookKey.M_GB_ITM.name()))
			return new ItemModel(overlay);
		if (property.equals(GradebookKey.M_USER.name()))
			return new LearnerModel(overlay);
		if (property.equals(GradebookKey.A_CLMNS.name()))
			return new FixedColumnModel(overlay);
		if (property.equals(GradebookKey.M_CONF.name()))
			return new ConfigurationModel(overlay);
		
		return new BaseModel();
	}

	public Item getCategoryItemModel(Long categoryId) {
		
		ItemModel gradebookItemModel = (ItemModel) this.getGradebookItemModel();
		
		if(null == gradebookItemModel)
			return null;
		
		List<ModelData> categoryItemModels = gradebookItemModel.getChildren();
		
		for(ModelData modelData : categoryItemModels) {
			
			ItemModel categoryItemModel = (ItemModel) modelData;
			
			Long categoryItemModelId = categoryItemModel.getCategoryId();
			
			if(null == categoryItemModelId)
				continue;
			
			if(0 == categoryItemModelId.compareTo(categoryId)) {
				return categoryItemModel;
			}
		}
		
		return null;
	}

	public String toXml() {
		return null;
	}
	
	public void fromXml(String xml) {}

	@Override
	public Item getItemByIdentifier(String id) {

		if (null == id)
			return null;
		
		ItemModel gradebookItemModel = (ItemModel) this.getGradebookItemModel();
		
		if(null == gradebookItemModel)
			return null;
				
		return findItemByIdAmongChildren(id, gradebookItemModel.getChildren());
		
	}

	private Item findItemByIdAmongChildren(String id, List<ModelData> children) {
		
		if (null == id || null == children) 
			return null;
		
		for (ModelData m : children) {
			if (id.equals(m.get(ItemKey.S_ID.name()))) {
				Item i = (Item) m;
				return i;
			} else {
				Item i = findItemByIdAmongChildren(id, ((ItemModel)m).getChildren());
				if (i != null) 
					return i;
			}
				
		}
		
		return null;
	}
	
	public String toString() {
		
		return getJSON();
	}
	


}
