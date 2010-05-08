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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.sakaiproject.component.gradebook.GradebookFrameworkServiceImpl;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.gradebook.gwt.sakai.model.Realm;
import org.sakaiproject.gradebook.gwt.sakai.model.RealmGroup;
import org.sakaiproject.gradebook.gwt.sakai.model.RealmRlGroupId;
import org.sakaiproject.gradebook.gwt.sakai.model.RealmRole;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.google.gwt.core.client.GWT;

public class GradebookFrameworkServiceMock extends
		GradebookFrameworkServiceImpl {

	private static final Log log = LogFactory
			.getLog(GradebookFrameworkServiceMock.class);
	protected UserDirectoryService userService;
	protected ToolManager toolManager;
	protected SiteService siteService;

	public void setUserService(UserDirectoryService userService) {
		this.userService = userService;
	}

	public void setToolManager(ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void addGradebook(final String uid, final String name) {
		super.addGradebook(uid, name);

		
		/*
		 * GRBK-603 
		 * - divides the available students across available groups
		 * - this number of the first users are added to the first group: #users%#groups
		 * - creates Student role (only)
		 */
		Site thisSite = null;
		try {
			thisSite = siteService.getSite(toolManager.getCurrentPlacement()
					.getContext());
		} catch (IdUnusedException e1) {
			e1.printStackTrace();
			return;
		}
		final Collection<Group> groups = thisSite.getGroups();
		final List<User> users = userService.getUsers();

		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {

				// Now let's create the appropriate realms for this site (this
				// is an odd place to do this, but we need a db connection, so
				// why not)

				RealmRole realmRole = new RealmRole();

				realmRole.setRoleName("Student");
				try {
					session.save(realmRole);
				} catch (Exception e) {
					log.error("Trying to save role:");
					e.printStackTrace();
				}

				Realm siteRealm = new Realm();
				
				siteRealm.setRealmId("/site/TESTSITECONTEXT");
				try {
					session.save(siteRealm);
				} catch (Exception e) {
					log.error("Trying to save realm:");
					e.printStackTrace();
				}

				// /divide them up among the available groups
				ArrayList<Group> groupList = new ArrayList<Group>(groups);
				
				Collection<User> addedToSiteRealm = new ArrayList<User>();

				// / save to realms tables
				int remainder = users.size() % groups.size();
				
				if (users.size() >= groupList.size()) {

					// /stuff the remainder in the first group

					for (int i = 0; i < remainder; ++i) {
						// / everyone gets added to the site realm
						User user = users.get(i);
						if (user != null) {
							addUserToRealmRole(user, siteRealm, realmRole,
									session);

							addedToSiteRealm.add(users.get(i));

							addUserToGroupInRole(users.get(i), groupList.get(0),
									realmRole, session);
						}
					}
					int share = new Double(Math.floor(users.size() / groups.size())).intValue();
					for(int g=0;g<groupList.size();++g) {
						for (int u=0;u<share;++u) {
							User user = users.get(u+remainder+share*g);
							if (user != null) {
								if (!addedToSiteRealm.contains(user)) {//siteRealm first
									addUserToRealmRole(user, siteRealm, realmRole, session);
									addedToSiteRealm.add(user);
								}
								addUserToGroupInRole(user, groupList.get(g), realmRole, session);
							}
						}
					}
				}

				return null;
			}

			
		});

	}

	
	private void addUserToRealmRole(final User user, final Realm siteRealm,
			final RealmRole realmRole, final Session session) {
		RealmGroup group = new RealmGroup();
		group = populateGroupFromUserRealmRole(group, user, siteRealm, realmRole);
		try {
			session.save(group);
		} catch (Exception e) {
			log.error("Trying to save realm(role)group:");
			e.printStackTrace();
		}

	}

	private RealmGroup populateGroupFromUserRealmRole(RealmGroup group, User user,
			Realm realm, RealmRole realmRole) {
		
		RealmRlGroupId id = new RealmRlGroupId();
		group.setRealmKey(realm.getRealmKey());
		group.setRoleKey(realmRole.getRoleKey());
		group.setUserId(user.getId());
		id.setRealmKey(realm.getRealmKey());
		id.setRoleKey(realmRole.getRoleKey());
		id.setUserId(user.getId());
		group.setRealm(realm);
		group.setId(id);
		group.setActive(Boolean.TRUE);
		
		return group;
	}
	
	@SuppressWarnings("unchecked")
	private void addUserToGroupInRole(final User user, final Group group, final RealmRole role,
			Session session) {

		Query q = session.createQuery("from Realm as r where r.realmId=:realmKey");
		q.setString("realmKey", group.getReference());
		
		
		Realm realm = null;
		try {	
			List<Realm> results = q.list();
			if (results != null && results.size() > 0 ) {
				realm = results.get(0);
			}
		} catch (Exception e) {
			log.error("Trying to retrieve realm:" + group.getReference());
			e.printStackTrace();
		}
		if (null == realm) {
			realm = new Realm();
			realm.setRealmId(group.getReference());
			try {
				session.save(realm);
			} catch (Exception e) {
				log.error("Trying to save realm:" + realm.getRealmId());
				e.printStackTrace();
			}
		}
		RealmGroup realmGroup = new RealmGroup();
		realmGroup = populateGroupFromUserRealmRole(realmGroup, user, realm, role);
		
		
		RealmGroup test = null;
		try {
			test = (RealmGroup) session.get(RealmGroup.class, realmGroup.getId());
		}  catch(Exception e) {
			log.error("Trying to retrieve realm with id:" + realmGroup.getId());
			e.printStackTrace();
		}
		if (null == test) {
			try {
				session.save(realmGroup);
			} catch (Exception e) {
				log.error("Trying to save realm(role)group:");
				e.printStackTrace();
			}
		}
	}

}
