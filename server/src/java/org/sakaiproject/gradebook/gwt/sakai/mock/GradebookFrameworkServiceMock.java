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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.sakaiproject.component.gradebook.GradebookFrameworkServiceImpl;
import org.sakaiproject.gradebook.gwt.sakai.model.Realm;
import org.sakaiproject.gradebook.gwt.sakai.model.RealmGroup;
import org.sakaiproject.gradebook.gwt.sakai.model.RealmRole;
import org.springframework.orm.hibernate3.HibernateCallback;

public class GradebookFrameworkServiceMock extends GradebookFrameworkServiceImpl {

	private static final Log log = LogFactory.getLog(GradebookFrameworkServiceMock.class);
		
	public void addGradebook(final String uid, final String name) {
		super.addGradebook(uid, name);
		
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
								
				// Now let's create the appropriate realms for this site (this is an odd place to do this, but we need a db connection, so why not)
				
				RealmRole realmRole = new RealmRole();
				realmRole.setRoleKey(Long.valueOf(1l));
				realmRole.setRoleName("Student");
				session.save(realmRole);
				
				
				for (int i=0;i<200;i++) {
					// This is actually totally wrong... in sakai there is only one realm per site, but it's a workaround
					// to Hibernate's screwy behavior with ids
					Realm realm = new Realm();
					realm.setRealmKey(Long.valueOf(i));
					realm.setRealmId("/site/TESTSITEID");
					session.save(realm);
					
					RealmGroup group = new RealmGroup();
					group.setRealmKey(Long.valueOf(i));
					group.setUserId(String.valueOf(i));
					group.setRoleKey(Long.valueOf(1l));
					group.setActive(Boolean.TRUE);
					session.save(group);
				}
				
				
				return null;
			}
		});
	}

}
