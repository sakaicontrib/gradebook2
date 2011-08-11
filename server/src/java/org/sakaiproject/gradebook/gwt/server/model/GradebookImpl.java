package org.sakaiproject.gradebook.gwt.server.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumn;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Statistics;
import org.sakaiproject.gradebook.gwt.client.model.key.GradebookKey;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.EntityType;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.server.Util;

public class GradebookImpl extends BaseModel implements Gradebook {

	private static final long serialVersionUID = 1L;

	public GradebookImpl() {
		super();
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
		return Util.toLong(get(GradebookKey.L_GB_ID.name()));
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
		return Util.toBoolean(get(GradebookKey.B_NEW_GB.name()));
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

	
	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Gradebook#getCategoryItemModel(java.lang.Long)
	 */
	public Item getCategoryItemModel(Long categoryId) {
		
		// Returning null because it's not called on the server side
		return null;
	}


	public String toXml() {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos));
	    encoder.writeObject(this);
	    encoder.flush();
	    encoder.close();
	    String xml = baos.toString();
		return xml;
	}

	@SuppressWarnings("unchecked")
	public void fromXml(String xml) {

		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));
		GradebookImpl source = (GradebookImpl)decoder.readObject();
		decoder.close();

		Map<String, Object> items = (Map<String, Object>) source.get(GradebookKey.M_GB_ITM.name());
				
		fixMap(items);
		
		put(GradebookKey.M_GB_ITM.name(), items);
		
        for (String key : this.keySet()) {
        	put(key, source.get(key));
        }
        
        
	}
	

	private void fixMap(Map<String, Object> items) {
		
		fixLeaves(items);
		
		fixBranches(items);
		
	}
	

	private void fixBranches(Map<String, Object> items) {
		
		for ( Object o : items.entrySet()) {
			if (o instanceof List<?>) {
				List<Map<String,Object>> l = (List<Map<String,Object>>)o;
				for (Object lo : l) {
					fixMap((Map)lo);
				}
				
			} else if (o instanceof Map<?, ?>) {
				fixMap((Map)o);
			}
		}
		
	}

	private void fixLeaves(Map<String, Object> items) {

		if (items.containsKey(ItemKey.S_ITM_TYPE.name())) {
			if (items.get(ItemKey.S_ITM_TYPE.name())
					.equals(EntityType.CATEGORY)) {
				items.put(ItemKey.S_PARENT.name(), get(ItemKey.S_NM.name()));

			} else if (items.get(ItemKey.S_ITM_TYPE.name()).equals(
					EntityType.GRADEBOOK.name())
					&& items.containsKey(ItemKey.S_ID.name())) {
				items.put(ItemKey.S_ID.name(), get(GradebookKey.S_GB_UID.name()));

			}
		}
		if (items.containsKey(GradebookKey.S_GB_UID.name())) {
			items.put(GradebookKey.S_GB_UID.name(), get(GradebookKey.S_GB_UID.name()));
		}
		if (items.containsKey(ItemKey.S_GB_NAME.name())) {
			items.put(ItemKey.S_GB_NAME.name(), get(ItemKey.S_NM.name()));
		}

	}

	/// TODO: This (server impl) has not been tested
	@Override
	public Item getItemByIdentifier(String id) {

		if (null == id)
			return null;
		
		GradeItem gradebookItemModel = (GradeItem) this.getGradebookItemModel();
		
		if(null == gradebookItemModel)
			return null;
				
		return findItemByIdAmongChildren(id, gradebookItemModel.getChildren());
		
	}

	private Item findItemByIdAmongChildren(String id, List<GradeItem> list) {
		
		if (null == id || null == list) 
			return null;
		
		for (GradeItem m : list) {
			if (id.equals(m.get(ItemKey.S_ID.name()))) {
				Item i = (Item) m.get(ItemKey.S_ID.name());
				return i;
			} else {
				Item i = findItemByIdAmongChildren(id, ((GradeItem)m).getChildren());
				if (i != null) 
					return i;
			}
				
		}
		
		
		return null;
		
	}

}
