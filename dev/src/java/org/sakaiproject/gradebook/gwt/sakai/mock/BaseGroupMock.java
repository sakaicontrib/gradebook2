package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.Set;
import java.util.Stack;

import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.Role;
import org.sakaiproject.authz.api.RoleAlreadyDefinedException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.impl.Identifiable;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.user.api.User;
import org.sakaiproject.util.BaseResourceProperties;
import org.sakaiproject.util.BaseResourcePropertiesEdit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gwt.core.client.GWT;

public class BaseGroupMock implements Group, Identifiable {

	public static String testSite_ContextId = "TESTSITECONTEXT";
	private String testGroupId = "TESTGROUP_" + Math.random()*System.currentTimeMillis();
	private BaseResourceProperties props = new BaseResourcePropertiesEdit();
	private String providerId = null;
	private Site site = null;
	private SiteService siteService = null;
	private String groupId = null;
	
	
	public BaseGroupMock(String providerId, SiteService siteService, Site site, String groupId) {
		this.providerId = "sectionEid: " + providerId;
		this.site = site;
		this.siteService = siteService;
		this.groupId = groupId;

	}

	public Site getContainingSite() {
		Site s = null;
		try {
			s = (new SiteServiceMock()).getSite(testSite_ContextId);
		}catch (IdUnusedException e) {}
		
		return s;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		return "sec.: " + getProviderGroupId();
	}

	public void setDescription(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setTitle(String arg0) {
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
		
		return testGroupId;
	}

	public ResourceProperties getProperties() {
		if (null == props) {
			props = new BaseResourcePropertiesEdit();
		}
		return (ResourceProperties) props;
	}

	public String getReference() {
		
		return siteService.siteGroupReference(site.getId(), getId());
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

	public Element toXml(Document arg0, Stack arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addMember(String arg0, String arg1, boolean arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	public Role addRole(String arg0) throws RoleAlreadyDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Role addRole(String arg0, Role arg1)
			throws RoleAlreadyDefinedException {
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

	public String getMaintainRole() {
		// TODO Auto-generated method stub
		return null;
	}

	public Member getMember(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Member> getMembers() {
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

	public String getProviderGroupId() {
		if (null == providerId) {
			providerId = "" + Math.floor(Math.random() * 4.5);
		}
		return providerId;
	}

	public Role getRole(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Role> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getRolesIsAllowed(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Role getUserRole(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getUsersHasRole(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getUsersIsAllowed(String arg0) {
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

	public void setProviderGroupId(String arg0) {
		// TODO Auto-generated method stub

	}

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
