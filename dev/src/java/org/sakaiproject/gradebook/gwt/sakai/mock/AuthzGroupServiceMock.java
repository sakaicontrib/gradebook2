package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.GroupAlreadyDefinedException;
import org.sakaiproject.authz.api.GroupFullException;
import org.sakaiproject.authz.api.GroupIdInvalidException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.javax.PagingPosition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AuthzGroupServiceMock extends HibernateDaoSupport implements AuthzGroupService {

	public AuthzGroup addAuthzGroup(String id) throws GroupIdInvalidException,
			GroupAlreadyDefinedException, AuthzPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthzGroup addAuthzGroup(String id, AuthzGroup other,
			String maintainUserId) throws GroupIdInvalidException,
			GroupAlreadyDefinedException, AuthzPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean allowAdd(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowJoinGroup(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemove(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUnjoinGroup(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdate(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public String authzGroupReference(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public int countAuthzGroups(String criteria) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set<String> getAllowedFunctions(String role,
			Collection<String> azGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthzGroup getAuthzGroup(String id) throws GroupNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getAuthzGroupIds(String providerId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AuthzGroup> getAuthzGroups(String criteria, PagingPosition page) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getAuthzGroupsIsAllowed(String userId, String function,
			Collection<String> azGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public List getAuthzUserGroupIds(
			ArrayList<String> authzGroupIds, final String userId) {
		HibernateCallback hc = new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query q = session.createQuery("select r.realmId from Realm as r,RealmGroup as rg where rg.userId=:userId "
						+ "and r.realmKey=rg.realmKey");
				q.setString("userId", userId);
				return q.list();
			}
			
			
			
		};
		return (List<String>) getHibernateTemplate().execute(hc);
		
	}

	public Set<String> getProviderIds(String authzGroupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Integer> getUserCountIsAllowed(String function,
			Collection<String> azGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserRole(String userId, String azGroupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getUsersIsAllowed(String function,
			Collection<String> azGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String[]> getUsersIsAllowedByGroup(String function,
			Collection<String> azGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getUsersRole(Collection<String> userIds,
			String azGroupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAllowed(String userId, String function, String azGroupId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAllowed(String userId, String function,
			Collection<String> azGroups) {
		// TODO Auto-generated method stub
		return false;
	}

	public void joinGroup(String authzGroupId, String role)
			throws GroupNotDefinedException, AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public void joinGroup(String authzGroupId, String role, int maxSize)
			throws GroupNotDefinedException, AuthzPermissionException,
			GroupFullException {
		// TODO Auto-generated method stub

	}

	public AuthzGroup newAuthzGroup(String id, AuthzGroup other,
			String maintainUserId) throws GroupAlreadyDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshUser(String userId) {
		// TODO Auto-generated method stub

	}

	public void removeAuthzGroup(AuthzGroup azGroup)
			throws AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public void removeAuthzGroup(String id) throws AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public void save(AuthzGroup azGroup) throws GroupNotDefinedException,
			AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public void unjoinGroup(String authzGroupId)
			throws GroupNotDefinedException, AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public Entity getEntity(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEntityAuthzGroups(Reference ref, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityDescription(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityUrl(Reference ref) {
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

	public boolean parseEntityReference(String reference, Reference ref) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean willArchiveMerge() {
		// TODO Auto-generated method stub
		return false;
	}

	public Map<String, String> getUserRoles(String arg0, Collection<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String archive(String arg0, Document arg1, Stack<Element> arg2,
			String arg3, List<Reference> arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	public String merge(String arg0, Element arg1, String arg2, String arg3,
			Map<String, String> arg4, Map<String, String> arg5, Set<String> arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<String> getAuthzUsersInGroups(Set<String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
