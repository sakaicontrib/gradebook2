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

package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.component.section.sakai.CourseImpl;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.impl.BaseSiteService;
import org.sakaiproject.site.impl.ResourceVector;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.BaseResourceProperties;
import org.sakaiproject.util.BaseResourcePropertiesEdit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SiteMock implements Site {

	private static final long serialVersionUID = 1L;
	
	private static final String GROUP_PROVIDER_PREFIX = "http://university.edu/section/abc";
	public static final String GROUP_ID_PREFIX = "48f8e8cd-0d5c-445a-bb53-2ca7ba618c8";
	private static final String GROUP_NAME_PREFIX = "Section ";
	
	private String id;
	private String title;
	private BaseResourceProperties props = new BaseResourcePropertiesEdit();
	private ResourceVector groups = null;
	private AuthzGroupService authzGroupService;
	private String providerId;

	public SiteMock(BaseSiteService siteService, AuthzGroupService authzGroupService) {
		props.addProperty(CourseImpl.EXTERNALLY_MAINTAINED, "true");
		props.addProperty(CourseImpl.STUDENT_REGISTRATION_ALLOWED, "false");
		props.addProperty(CourseImpl.STUDENT_SWITCHING_ALLOWED, "false");
	}

	@SuppressWarnings("unchecked")
	public SiteMock(String id, String title, BaseSiteService siteService, AuthzGroupService authzGroupService) {
		this.id = id;
		this.title = title;
		this.authzGroupService = authzGroupService;
		props.addProperty(CourseImpl.EXTERNALLY_MAINTAINED, "true");
		props.addProperty(CourseImpl.STUDENT_REGISTRATION_ALLOWED, "false");
		props.addProperty(CourseImpl.STUDENT_SWITCHING_ALLOWED, "false");
		groups = new ResourceVector();
		StringBuilder providerId = new StringBuilder();
		
		for (int i = 0; i < SectionAwarenessMock.NUMBER_OF_MOCK_SECTIONS; ++i) {
			
			String groupProviderId = GROUP_PROVIDER_PREFIX + Integer.toString(i);
			String groupId = GROUP_ID_PREFIX + Integer.toString(i);
			String groupName = GROUP_NAME_PREFIX + Integer.toString(i);
			
			groups.add(new BaseGroupMock(groupProviderId , siteService, this, groupId, groupName));
			
			if (i>0)
				providerId.append("+");
			
			providerId.append(groupProviderId);
		}
		setProviderGroupId(providerId.toString());
		
	}

	public Group addGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	public SitePage addPage() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getCreatedBy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getCreatedTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public Group getGroup(String id) {
		
		if (id == null) return null;

		//?!!!NOT THIS IS NOT checking for references!! See BaseSite.getGroup(id)
		return (Group) ((ResourceVector) getGroups()).getById(id);
	}

	public Collection getGroups() {
		return groups;
	}

	@SuppressWarnings("unchecked")
	public Collection getGroupsWithMember(String userId) {
		Collection siteGroups = getGroups();
		ArrayList<String> siteGroupRefs = new ArrayList<String>(siteGroups.size());
		for ( Iterator it=siteGroups.iterator(); it.hasNext(); )
			siteGroupRefs.add( ((Group)it.next()).getReference() );
			
		List groups = authzGroupService.getAuthzUserGroupIds(siteGroupRefs, userId);
		Collection<Group> rv = new Vector<Group>();
		for (Iterator i = groups.iterator(); i.hasNext();)
		{
			Member m = null;
			Group g = getGroup( (String)i.next() );
			
			if ( g != null )
				m = g.getMember(userId);
			if ((m != null) && (m.isActive()))
				rv.add(g);
		}

		return rv;
	}

	public Collection getGroupsWithMemberHasRole(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIconUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIconUrlFull() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInfoUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInfoUrlFull() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getJoinerRole() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getModifiedBy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getModifiedTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getOrderedPages() {
		// TODO Auto-generated method stub
		return null;
	}

	public SitePage getPage(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getPages() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getShortDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSkin() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		return title;
	}

	public ToolConfiguration getTool(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ToolConfiguration getToolForCommonId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getTools(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getTools(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasGroups() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCustomPageOrdered() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isJoinable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPubView() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPublished() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isType(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void loadAll() {
		// TODO Auto-generated method stub

	}

	public void regenerateIds() {
		// TODO Auto-generated method stub

	}

	public void removeGroup(Group arg0) {
		// TODO Auto-generated method stub

	}

	public void removePage(SitePage arg0) {
		// TODO Auto-generated method stub

	}

	public void setCustomPageOrdered(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public void setDescription(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setIconUrl(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setInfoUrl(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setJoinable(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public void setJoinerRole(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setPubView(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public void setPublished(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public void setShortDescription(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setSkin(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setTitle(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setType(String arg0) {
		// TODO Auto-generated method stub

	}

	public ResourcePropertiesEdit getPropertiesEdit() {
		if (null == props) {
			props = new BaseResourcePropertiesEdit();
		}
		return (ResourcePropertiesEdit) props;
	}

	public boolean isActiveEdit() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getId() {
		return id;
	}

	public ResourceProperties getProperties() {
		if (null == props) {
			props = new BaseResourcePropertiesEdit();
		}
		return props;
	}

	public String getReference() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReference(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addMember(String arg0, String arg1, boolean arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	public Role addRole(String arg0) throws RoleAlreadyDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Role addRole(String arg0, Role arg1) throws RoleAlreadyDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMaintainRole() {
		// TODO Auto-generated method stub
		return null;
	}

	public Member getMember(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getMembers() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProviderGroupId() {
		return providerId;
	}

	public Role getRole(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getRolesIsAllowed(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Role getUserRole(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getUsersHasRole(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getUsersIsAllowed(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasRole(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAllowed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean keepIntersection(AuthzGroup arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeMember(String arg0) {
		// TODO Auto-generated method stub

	}

	public void removeMembers() {
		// TODO Auto-generated method stub

	}

	public void removeRole(String arg0) {
		// TODO Auto-generated method stub

	}

	public void removeRoles() {
		// TODO Auto-generated method stub

	}

	public void setMaintainRole(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setProviderGroupId(String providerId) {
		this.providerId = providerId;

	}

	public Date getCreatedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getModifiedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Element toXml(Document arg0, Stack<Element> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getSoftlyDeletedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSoftlyDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSoftlyDeleted(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	public Collection<String> getMembersInGroups(Set<String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHtmlDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHtmlShortDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
