package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteAdvisor;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SiteServiceMock implements SiteService {

	public Site addSite(String arg0, String arg1) throws IdInvalidException,
			IdUsedException, PermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public Site addSite(String arg0, Site arg1) throws IdInvalidException,
			IdUsedException, PermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addSiteAdvisor(SiteAdvisor arg0) {
		// TODO Auto-generated method stub

	}

	public boolean allowAccessSite(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowAddCourseSite() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowAddSite(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemoveSite(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRoleSwap(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUnjoinSite(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateGroupMembership(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateSite(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateSiteMembership(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowViewRoster(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public int countSites(SelectionType arg0, Object arg1, String arg2,
			Map<String, String> arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Group findGroup(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public SitePage findPage(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ToolConfiguration findTool(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getLayoutNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Site getSite(String context) throws IdUnusedException {
		return new SiteMock(context);
	}

	public List<SiteAdvisor> getSiteAdvisors() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteDisplay(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteSkin(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteSpecialId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getSiteTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteUserId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Site getSiteVisit(String arg0) throws IdUnusedException,
			PermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Site> getSites(SelectionType arg0, Object arg1, String arg2,
			Map<String, String> arg3, SortType arg4, PagingPosition arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSpecialSiteId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserSiteId(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSpecialSite(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUserSite(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void join(String arg0) throws IdUnusedException, PermissionException {
		// TODO Auto-generated method stub

	}

	public String merge(String arg0, Element arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeSite(Site arg0) throws PermissionException {
		// TODO Auto-generated method stub

	}

	public boolean removeSiteAdvisor(SiteAdvisor arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void save(Site arg0) throws IdUnusedException, PermissionException {
		// TODO Auto-generated method stub

	}

	public void saveGroupMembership(Site arg0) throws IdUnusedException,
			PermissionException {
		// TODO Auto-generated method stub

	}

	public void saveSiteInfo(String arg0, String arg1, String arg2)
			throws IdUnusedException, PermissionException {
		// TODO Auto-generated method stub

	}

	public void saveSiteMembership(Site arg0) throws IdUnusedException,
			PermissionException {
		// TODO Auto-generated method stub

	}

	public void setSiteSecurity(String arg0, Set<String> arg1,
			Set<String> arg2, Set<String> arg3) {
		// TODO Auto-generated method stub

	}

	public void setUserSecurity(String arg0, Set<String> arg1,
			Set<String> arg2, Set<String> arg3) {
		// TODO Auto-generated method stub

	}

	public boolean siteExists(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public String siteGroupReference(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String sitePageReference(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String siteReference(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String siteToolReference(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void unjoin(String arg0) throws IdUnusedException,
			PermissionException {
		// TODO Auto-generated method stub

	}

	public String archive(String arg0, Document arg1, Stack arg2, String arg3,
			List arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity getEntity(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEntityAuthzGroups(Reference arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityDescription(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityUrl(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpAccess getHttpAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String merge(String arg0, Element arg1, String arg2, String arg3,
			Map arg4, Map arg5, Set arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean parseEntityReference(String arg0, Reference arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean willArchiveMerge() {
		// TODO Auto-generated method stub
		return false;
	}

}
