/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006, 2008, 2009 The Regents of the University of California, The MIT Corporation
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
package org.sakaiproject.gradebook.gwt.sakai.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.TransientObjectException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.sakaiproject.gradebook.gwt.sakai.GradeCalculations;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Authn;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.gradebook.gwt.sakai.model.UserConfiguration;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereference;
import org.sakaiproject.gradebook.gwt.sakai.model.UserDereferenceRealmUpdate;
import org.sakaiproject.gradebook.gwt.server.Util;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.service.gradebook.shared.ConflictingAssignmentNameException;
import org.sakaiproject.service.gradebook.shared.ConflictingCategoryNameException;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.service.gradebook.shared.StaleObjectModificationException;
import org.sakaiproject.tool.gradebook.AbstractGradeRecord;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.AssignmentGradeRecord;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Comment;
import org.sakaiproject.tool.gradebook.CourseGrade;
import org.sakaiproject.tool.gradebook.CourseGradeRecord;
import org.sakaiproject.tool.gradebook.GradableObject;
import org.sakaiproject.tool.gradebook.GradeMapping;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.GradingEvent;
import org.sakaiproject.tool.gradebook.LetterGradePercentMapping;
import org.sakaiproject.tool.gradebook.Permission;
import org.sakaiproject.tool.gradebook.facades.EventTrackingService;
import org.sakaiproject.user.api.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * This is the Hibernate implementation of the GradebookToolService for Gradebook2. At the time of this
 * writing 1/2/2009 it represents a subset of the functionality provided by the old Gradebook services (GradebookManagerHibernateImpl.java
 * and BaseHibernateManager.java) and licensed under ECL 1.0. 
 * 
 * Notable modifications from those earlier class files include:
 * - The addition of Java 1.5 Generics to most method signatures
 * 
 */
public class GradebookToolServiceImpl extends HibernateDaoSupport implements GradebookToolService {

	private static final Log log = LogFactory.getLog(GradebookToolServiceImpl.class);
	// Special logger for data contention analysis.
	private static final Log logData = LogFactory.getLog(GradebookToolServiceImpl.class.getName() + ".GB_DATA");

	public static int MAX_NUMBER_OF_SQL_PARAMETERS_IN_LIST = 1000;

	protected SectionAwareness sectionAwareness;
	protected Gradebook2Authn authn;
	protected EventTrackingService eventTrackingService;
	protected GradeCalculations gradeCalculations;

	// Local cache of static-between-deployment properties.
	//protected Map propertiesMap = new HashMap();
	//protected Map<MultiKey, Boolean> isStudentGradeCache = new ConcurrentHashMap<MultiKey, Boolean>();


	/*
	 * GRADEBOOKTOOLSERVICE IMPLEMENTATION
	 * 
	 */

	public Long createAssignmentForCategory(final Long gradebookId, final Long categoryId,
			final String name, final Double points, final Double weight, final Date dueDate,
			final Boolean isUnweighted, final Boolean isExtraCredit, final Boolean isNotCounted, 
			final Boolean isReleased, final Integer itemOrder, final Boolean isNullsAsZeros) throws ConflictingAssignmentNameException, StaleObjectModificationException, IllegalArgumentException
			{
		if(gradebookId == null)
		{
			throw new IllegalArgumentException("gradebookId or categoryId is null in BaseHibernateManager.createAssignmentForCategory");
		}

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Gradebook gb = (Gradebook)session.load(Gradebook.class, gradebookId);
				Category cat = null;

				if (categoryId != null)
					cat = (Category)session.load(Category.class, categoryId);

				Assignment asn = new Assignment();
				asn.setGradebook(gb);
				if (cat != null)
					asn.setCategory(cat);
				asn.setName(name.trim());
				asn.setPointsPossible(points);
				asn.setAssignmentWeighting(weight);
				asn.setDueDate(dueDate);
				asn.setNotCounted(Util.checkBoolean(isUnweighted));
				
				// GRBK-833: All grade items in an extra credit category are by default
				// extra credit items. Here we set this explicitly. We can remove this once
				// the UI handles this correctly
				if(cat != null && Util.checkBoolean(cat.isExtraCredit())) {
					asn.setExtraCredit(Boolean.TRUE);
				}
				else {

					asn.setExtraCredit(isExtraCredit);
				}
				
				asn.setUngraded(false);
				if (isNotCounted != null) {
					asn.setNotCounted(isNotCounted.booleanValue());
				}
				asn.setSortOrder(itemOrder);
				asn.setCountNullsAsZeros(isNullsAsZeros);
				
				if(isReleased!=null){
					asn.setReleased(isReleased.booleanValue());
				}

				Long id = (Long)session.save(asn);

				return id;
			}
		};

		return (Long)getHibernateTemplate().execute(hc);
	}

	public Long createCategory(final Long gradebookId, final String name, final Double weight, final Integer dropLowest, final Boolean equalWeightAssignments, final Boolean isUnweighted, final Boolean isExtraCredit, final Integer categoryOrder, final Boolean isEnforcePointWeighting) 
	throws ConflictingCategoryNameException, StaleObjectModificationException {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Gradebook gb = (Gradebook)session.load(Gradebook.class, gradebookId);
				List conflictList = ((List)session.createQuery(
				"select ca from Category as ca where ca.name = ? and ca.gradebook = ? and (ca.removed=false or ca.removed is null) ").
				setString(0, name).
				setEntity(1, gb).list());
				int numNameConflicts = conflictList.size();
				if(numNameConflicts > 0) {
					throw new ConflictingCategoryNameException("You cannot save multiple categories in a gradebook with the same name");
				}
				if (weight != null) {
					if ( weight.intValue() > 1 || weight.intValue() < 0)
					{
						throw new IllegalArgumentException("weight for category is greater than 1 or less than 0 in createCategory of BaseHibernateManager");
					}
				}

				int dl = dropLowest == null ? 0 : dropLowest.intValue();

				Category ca = new Category();
				ca.setGradebook(gb);
				ca.setName(name);
				ca.setWeight(weight);
				ca.setDrop_lowest(dl);
				ca.setEqualWeightAssignments(equalWeightAssignments);
				ca.setUnweighted(isUnweighted);
				ca.setRemoved(false);
				ca.setExtraCredit(isExtraCredit);
				ca.setCategoryOrder(categoryOrder);
				ca.setEnforcePointWeighting(isEnforcePointWeighting);

				Long id = (Long)session.save(ca);

				return id;
			}
		};

		return (Long)getHibernateTemplate().execute(hc);
	}


	public void createOrUpdateUserConfiguration(final String userUid, final Long gradebookId, final String configField, final String configValue) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				Query q = session.createQuery("from UserConfiguration as config where config.userUid = :userUid and config.gradebookId = :gradebookId and config.configField = :configField ");
				q.setString("userUid", userUid);
				q.setLong("gradebookId", gradebookId);
				q.setString("configField", configField);

				UserConfiguration config = null;

				synchronized(this) {

					config = (UserConfiguration)q.uniqueResult();

					if (config == null) {

						config = new UserConfiguration();
						config.setUserUid(userUid);
						config.setGradebookId(gradebookId);
						config.setConfigField(configField);
						config.setConfigValue(configValue);
						session.save(config);
						// After persisting the object we can return so that don't do the update bellow
						return null;
					}
				}

				// At this point we retrieved the existing config object and just update the configValue
				config.setConfigValue(configValue);
				session.update(config);

				return null;

			}
		};

		getHibernateTemplate().execute(hc);
	}


	public List<ActionRecord> getActionRecords(final String gradebookUid, final int offset, final int limit) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery("from ActionRecord as ar where ar.gradebookUid=:gradebookUid order by ar.dateRecorded desc ");
				q.setString("gradebookUid", gradebookUid);
				q.setFirstResult(offset);
				q.setMaxResults(limit);
				return q.list();
			}
		};
		return (List<ActionRecord>)getHibernateTemplate().execute(hc);	
	}

	public Integer getActionRecordSize(final String gradebookUid) {
		Number size = (Number)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery("select count(*) from ActionRecord as ar where ar.gradebookUid=:gradebookUid ");
				q.setString("gradebookUid", gradebookUid);
				return (Number) q.iterate().next();
			}
		});

		return size == null ? 0 : Integer.valueOf(size.intValue());
	}

	public UserDereferenceRealmUpdate getLastUserDereferenceSync(final String siteId, final String realmGroupId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String realmId = realmGroupId;

				if (realmGroupId == null)
					realmId = new StringBuffer().append("/site/").append(siteId).toString();
				else if (siteId == null) {
					if (log.isInfoEnabled())
						log.info("No siteId defined");
					return null;
				}

				Criteria criteria = session.createCriteria(UserDereferenceRealmUpdate.class);
				criteria.add(Restrictions.eq("realmId", realmId));

				return criteria.uniqueResult();
			}
		};
		
		UserDereferenceRealmUpdate userDereferenceRealmUpdate = null;

		try {
			
			userDereferenceRealmUpdate = (UserDereferenceRealmUpdate)getHibernateTemplate().execute(hc);
		}
		catch(IncorrectResultSizeDataAccessException irsdae) {
			log.warn("Did not return a unique result" , irsdae);
		}

		return userDereferenceRealmUpdate;
	}

	public void syncUserDereferenceBySite(final String siteId, final String realmGroupId, final List<User> users, final int realmCount, final String[] roleNames) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				
				String realmId = realmGroupId;

				if (realmGroupId == null)
					realmId = new StringBuffer().append("/site/").append(siteId).toString();
				else if (siteId == null) {
					if (log.isInfoEnabled())
						log.info("No siteId defined");
					return new ArrayList<AssignmentGradeRecord>();
				}

				StringBuilder builder = new StringBuilder()
				.append("select user from Realm as r, RealmGroup rg, UserDereference user ")
				.append("where rg.realmKey=r.realmKey ")
				.append("and r.realmId=:realmId ")
				.append("and user.userUid=rg.userId ")
				.append("and rg.active=true ");

				Query query = null;

				try {
					query = session.createQuery(builder.toString());
				}
				catch(HibernateException he) {
					log.warn("Exception occured during createQuery() call", he);
				}
				
				if(null == query) {
					return null;
				}
				
				query.setString("realmId", realmId);

				List<UserDereference> userDereferences = query.list();
				Map<String, UserDereference> userDereferenceMap = new HashMap<String, UserDereference>();
				for (UserDereference user : userDereferences) {
					userDereferenceMap.put(user.getUserUid(), user);
				}

				Set<String> addedUserIds = new HashSet<String>(userDereferenceMap.keySet());

				int i=0;
				for (User user : users) {
					UserDereference dereference = userDereferenceMap.get(user.getId());

					String lastName = user.getLastName() == null ? "" : user.getLastName();
					String firstName = user.getFirstName() == null ? "" : user.getFirstName();

					String sortName = new StringBuilder().append(lastName.toUpperCase()).append("   ").append(firstName.toUpperCase()).toString();
					String lastNameFirst = new StringBuilder().append(lastName).append(", ").append(firstName).toString();

					if (lastName.equals("") && firstName.equals("")) {
						sortName = user.getEid();
						lastNameFirst = user.getEid();
					}

					if (dereference == null) {

						dereference = new UserDereference(user.getId(), user.getEid(), user.getDisplayId(), user.getDisplayName(), lastNameFirst, sortName, user.getEmail());

						if (!addedUserIds.contains(user.getId())) {
							try {
								session.save(dereference);
								i++;
							} catch (ConstraintViolationException he) {
								log.info("Caught a constraint violation exception trying to save a user record. This is not necessarily a bug. ", he);
							}

							if ( i % 20 == 0 ) { //20, same as the JDBC batch size
								//flush a batch of inserts/updates and release memory:
								try {
									session.flush();
									session.clear();
								} catch (ConstraintViolationException he) {
									log.info("Caught a constraint violation exception trying to save a user record. This is not necessarily a bug. ", he);
								}
							}

							addedUserIds.add(user.getId());
						}
					} else {

						boolean isModified = false;

						if (!user.getEid().equals(dereference.getEid())) {
							dereference.setEid(user.getEid());
							isModified = true;
						}

						if (!user.getDisplayId().equals(dereference.getDisplayId())) {
							dereference.setDisplayId(user.getDisplayId());
							isModified = true;
						}

						if (!user.getDisplayName().equals(dereference.getDisplayName())) {
							dereference.setDisplayName(user.getDisplayName());
							isModified = true;
						}

						if (!lastNameFirst.equals(dereference.getLastNameFirst())) {
							dereference.setLastNameFirst(lastNameFirst);
							isModified = true;
						} 

						if (!sortName.equals(dereference.getSortName())) {
							dereference.setSortName(sortName);
							isModified = true;
						}

						if (!user.getEmail().equals(dereference.getEmail())) {
							dereference.setEmail(user.getEmail());
							isModified = true;
						}

						if (isModified) {
							try {
								session.update(dereference);
								i++;
							} catch (ConstraintViolationException he) {
								log.info("Caught a constraint violation exception trying to save a user record. This is not necessarily a bug. ", he);
							}

							if ( i % 20 == 0 ) { //20, same as the JDBC batch size
								try {
									//flush a batch of inserts/updates and release memory:
									session.flush();
									session.clear();
								} catch (ConstraintViolationException he) {
									log.info("Caught a constraint violation exception trying to save a user record. This is not necessarily a bug. ", he);
								}
							}
						}

						// Now we remove the keyed value from the map so we know it should stay in DB
						userDereferenceMap.remove(user.getId());
					}
				}

				// Finally, we remove any dereferences that are still in the map
				for (UserDereference dereference : userDereferenceMap.values()) {
					
					try {
						session.delete(dereference);
					}
					catch(HibernateException he) {
						log.warn("Exception occured during a UserDereference deletion", he);
					}
					
					i++;

					if ( i % 20 == 0 ) { //20, same as the JDBC batch size
						//flush a batch of deletes and release memory:
						try {
							session.flush();
							session.clear();
						}
						catch(HibernateException he) {
							log.warn("Exception occured during a UserDereference deletion (flush/clear)", he);
						}
					}
				}


				Criteria criteria = session.createCriteria(UserDereferenceRealmUpdate.class);
				criteria.add(Restrictions.eq("realmId", realmId));

				UserDereferenceRealmUpdate lastUpdate = null;
				
				try {
					lastUpdate = (UserDereferenceRealmUpdate)criteria.uniqueResult();
				}
				catch(IncorrectResultSizeDataAccessException irsdae) {
					
					log.warn("Did not return a unique result" , irsdae);
				}

				if (lastUpdate == null) {
					
					lastUpdate = new UserDereferenceRealmUpdate(realmId, Integer.valueOf(realmCount));

					try {
						session.save(lastUpdate);
					}
					catch(ConstraintViolationException cve) {
						
						log.info("Caught a constraint violation exception trying to save a UserDereferenceRealmUpdate record. This is not necessarily a bug. ", cve);
					}
					
				} else {
					
					lastUpdate.setRealmCount(Integer.valueOf(realmCount));
					lastUpdate.setLastUpdate(new Date());
					
					try {
						session.update(lastUpdate);
					}
					catch(ConstraintViolationException cve) {

						log.info("Caught a constraint violation exception trying to save a UserDereferenceRealmUpdate record. This is not necessarily a bug. ", cve);
					}
				}

				return null;
			}
		};

		try {
			getHibernateTemplate().execute(hc);
		} 
		catch (ConstraintViolationException he) {
			log.info("Caught a constraint violation exception trying to save a user record. This is not necessarily a bug. ", he);
		}
		catch(IncorrectResultSizeDataAccessException irsdae)  {
			log.warn("Did not return a unique result", irsdae);
		}
	}


	public List<String> getFullUserListForSite(final String siteId, final String[] roleNames) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				String realmId = new StringBuffer().append("/site/").append(siteId).toString();


				Query query = null;

				StringBuilder builder = new StringBuilder()
				.append("select rg.userId from Realm as r, RealmGroup rg, RealmRole rr ")
				.append("where rg.realmKey=r.realmKey ")
				.append("and r.realmId=:realmId ")
				.append("and rr.roleKey = rg.roleKey ")
				.append("and rr.roleName in (:roleKeys) ")
				.append("and rg.active=true ");

				query = session.createQuery(builder.toString());
				query.setString("realmId", realmId);
				query.setParameterList("roleKeys", roleNames);

				return query.list();
			}
		};

		List<String> result = (List<String>)getHibernateTemplate().execute(hc);

		return result;
	}
	
	public List<String> getUserListForSections(final String[] roleNames, final String[] realmIds) {
		
		HibernateCallback hc = new HibernateCallback() {
		
			public Object doInHibernate(Session session) throws HibernateException {

				Query query = null;

				StringBuilder builder = new StringBuilder()
				.append("select rg.userId from Realm as r, RealmGroup rg, RealmRole rr ")
				.append("where rg.realmKey = r.realmKey ")
				.append("and rr.roleKey = rg.roleKey ")
				.append("and rr.roleName in (:roleNames) ")
				.append("and r.realmId in (:realmIds) ")
				.append("and rg.active=true ");

				query = session.createQuery(builder.toString());
				query.setParameterList("realmIds", realmIds);
				query.setParameterList("roleNames", roleNames);
				
				return query.list();
			}
		};

		List<String> result = (List<String>)getHibernateTemplate().execute(hc);

		return result;
	}

	//FIXME: this might be better done with Site.getMembers()
	public int getFullUserCountForSite(final String siteId, final String realmGroupId, final String[] roleNames) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				String realmId = realmGroupId;

				if (realmGroupId == null)
					realmId = new StringBuffer().append("/site/").append(siteId).toString();
				else if (siteId == null) {
					if (log.isInfoEnabled())
						log.info("No siteId defined");
					return Integer.valueOf(0);
				}

				Query query = null;

				StringBuilder builder = new StringBuilder()
				.append("select count(*) from Realm as r, RealmGroup rg, RealmRole rr ")
				.append("where rg.realmKey=r.realmKey ")
				.append("and r.realmId=:realmId ")
				.append("and rr.roleKey = rg.roleKey ")
				.append("and rr.roleName in (:roleKeys) ")
				.append("and rg.active=true ");

				query = session.createQuery(builder.toString());
				query.setString("realmId", realmId);
				query.setParameterList("roleKeys", roleNames);

				Number realmCount = (Number)query.uniqueResult();

				return realmCount;
			}
		};
		Number result = (Number)getHibernateTemplate().execute(hc);

		return result == null ? 0 : result.intValue();
	}

	public int getDereferencedUserCountForSite(final String siteId, final String realmGroupId, final String[] roleNames) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				String realmId = realmGroupId;

				if (realmGroupId == null)
					realmId = new StringBuffer().append("/site/").append(siteId).toString();
				else if (siteId == null) {
					if (log.isInfoEnabled())
						log.info("No siteId defined");
					return new ArrayList<AssignmentGradeRecord>();
				}

				Query query = null;

				StringBuilder builder = new StringBuilder()
				.append("select count(*) from Realm as r, RealmGroup rg, RealmRole rr, UserDereference u ")
				.append("where rg.realmKey=r.realmKey ")
				.append("and u.userUid = rg.userId ")
				.append("and r.realmId=:realmId ")
				.append("and rr.roleKey = rg.roleKey ")
				.append("and rr.roleName in (:roleKeys) ")
				.append("and rg.active=true ");

				query = session.createQuery(builder.toString());
				query.setString("realmId", realmId);
				query.setParameterList("roleKeys", roleNames);

				Number realmCount = (Number)query.uniqueResult();

				return realmCount;
			}
		};
		Number result = (Number)getHibernateTemplate().execute(hc);

		return result == null ? 0 : result.intValue();
	}

	public List<UserConfiguration> getUserConfigurations(final String userUid, final Long gradebookId) {

		if (gradebookId == null || userUid == null)
			return new ArrayList<UserConfiguration>();

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				Query query = null;

				StringBuilder builder = new StringBuilder()
				.append("select config ")
				.append("from UserConfiguration config ")
				.append("where config.userUid = :userUid ")
				.append("and config.gradebookId = :gradebookId ");

				query = session.createQuery(builder.toString());
				query.setString("userUid", userUid);
				query.setLong("gradebookId", gradebookId);

				return query.list();
			}
		};
		return (List<UserConfiguration>)getHibernateTemplate().execute(hc);
	}

	/// FIXME: check Site API for the same thing
	public int getUserCountForSite(final String[] realmIds, final String sortField, 
			final String searchField, final String searchCriteria, final String[] roleNames) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				Query query = null;

				// GRBK-233 : in case a site has sections as well as adhoc groups, we need to make sure 
				// that we don't get double entries. Thus I added the "distinct" clause
				StringBuilder builder = new StringBuilder()
				.append("select count(distinct user) from Realm as r, RealmGroup rg, RealmRole rr, UserDereference user ")
				.append("where rg.realmKey=r.realmKey ")
				.append("and r.realmId in (:realmIds) ")
				.append("and user.userUid=rg.userId ")
				.append("and rr.roleKey = rg.roleKey ")
				.append("and rr.roleName in (:roleKeys) ")
				.append("and rg.active=true ");

				if (searchField != null && searchCriteria != null) {
					builder.append("and lower(user.").append(searchField).append(") like lower(:searchCriteria) ");
				}

				query = session.createQuery(builder.toString());
				query.setParameterList("realmIds", realmIds);
				query.setParameterList("roleKeys", roleNames);

				if (searchField != null && searchCriteria != null) {
					String criteria = new StringBuilder().append("%").append(searchCriteria).append("%").toString();
					query.setParameter("searchCriteria", criteria);
				}
				
				Number realmCount = (Number)query.uniqueResult();

				return realmCount;
			}
		};

		Number result = (Number)getHibernateTemplate().execute(hc);

		return result == null ? 0 : result.intValue();
	}


	public List<Object[]> getUserGroupReferences(final List<String> groupReferences, final String[] roleNames) {

		if (groupReferences == null || groupReferences.size() == 0)
			return new ArrayList<Object[]>();


			HibernateCallback hc = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {

					Query query = null;

					StringBuilder builder = new StringBuilder()
					.append("select rg.userId, r.realmId ")
					.append("from Realm as r, RealmGroup as rg, RealmRole rr ")
					.append("where rg.realmKey=r.realmKey ")
					.append("and r.realmId in (:groupReferences) ")
					.append("and rr.roleKey = rg.roleKey ")
					.append("and rr.roleName in (:roleKeys) ")
					.append("and rg.active=true ");

					query = session.createQuery(builder.toString());
					query.setParameterList("groupReferences", groupReferences);
					query.setParameterList("roleKeys", roleNames);

					return query.list();
				}
			};
			return (List<Object[]>)getHibernateTemplate().execute(hc);
	}


	public List<UserDereference> getUserDereferences(final String[] realmIds, final String sortField, final String searchField, 
			final String searchCriteria, final int offset, final int limit, final boolean isAsc, final String[] roleNames) {

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				Query query = null;

				// GRBK-233 : in case a site has sections as well as adhoc groups, we need to make sure 
				// that we don't get double entries. Thus I added the "distinct" clause
				StringBuilder builder = new StringBuilder()
				.append("select distinct user from Realm as r, RealmGroup rg, RealmRole rr, UserDereference user ")
				.append("where rg.realmKey = r.realmKey ")
				.append("and r.realmId in (:realmIds) ")
				.append("and user.userUid = rg.userId ")
				.append("and rr.roleKey = rg.roleKey ")
				.append("and rr.roleName in (:roleKeys) ")
				.append("and rg.active = true ");

				if (searchField != null && searchCriteria != null) 
					builder.append("and lower(user.").append(searchField).append(") like lower(:searchCriteria) ");
				
				if (sortField != null) {

					builder.append("order by user.").append(sortField);

					if (isAsc)
						builder.append(" asc ");
					else
						builder.append(" desc ");

				}

				query = session.createQuery(builder.toString());

				query.setParameterList("realmIds", realmIds);	
				query.setParameterList("roleKeys", roleNames);

				if (searchField != null && searchCriteria != null) {
					String criteria = new StringBuilder().append("%").append(searchCriteria).append("%").toString();
					query.setParameter("searchCriteria", criteria);
				}
				
				if (offset != -1)
					query.setFirstResult(offset);
				if (limit != -1)
					query.setMaxResults(limit);

				return query.list();
			}
		};
		return (List<UserDereference>)getHibernateTemplate().execute(hc);
	}


	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(final Long[] assignmentIds) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				Query query = null;

				StringBuilder builder = new StringBuilder();

				builder.append("select agr from AssignmentGradeRecord agr ")
				.append(" where agr.gradableObject.id in (:assignmentIds) ");

				query = session.createQuery(builder.toString());

				query.setParameterList("assignmentIds", assignmentIds);

				return query.list();
			}
		};
		return (List<AssignmentGradeRecord>)getHibernateTemplate().execute(hc);
	}

	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(final Long[] assignmentIds, final String[] realmIds) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				Query query = null;

				StringBuilder builder = new StringBuilder();

				builder.append("select agr from AssignmentGradeRecord as agr, Realm as r, RealmGroup as rg ")
				.append(" where agr.studentId = rg.userId ")
				.append(" and r.realmKey = rg.realmKey ")
				.append(" and agr.gradableObject.id in (:assignmentIds) ")
				.append(" and r.realmId in (:realmIds) ");
				
				query = session.createQuery(builder.toString());
				query.setParameterList("assignmentIds", assignmentIds);
				query.setParameterList("realmIds", realmIds);

				return query.list();
			}
		};
		return (List<AssignmentGradeRecord>)getHibernateTemplate().execute(hc);
		
	}

	// GRBK-40 : TPA : Eliminated the in java filtering
	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(final Long gradebookId, final String[] realmIds, final String[] roleNames) {
		HibernateCallback hc = new HibernateCallback() {
			
			public Object doInHibernate(Session session) throws HibernateException {

				Query query = null;

				StringBuilder builder = new StringBuilder();

				builder.append(" select agr from AssignmentGradeRecord agr, GradableObject go, Realm as r, RealmGroup rg, RealmRole rr ")
				.append(" where agr.gradableObject = go.id ")
				.append(" and agr.studentId = rg.userId ")
				.append(" and rg.roleKey = rr.roleKey ")
				.append(" and r.realmKey = rg.realmKey ")
				.append(" and go.gradebook.id=:gradebookId ")
				.append(" and r.realmId in (:realmIds) ")
				.append(" and (go.removed=false or go.removed is null) ")
				.append(" and rr.roleName in (:roleKeys) ");

				query = session.createQuery(builder.toString());
				query.setLong("gradebookId", gradebookId.longValue());
				query.setParameterList("realmIds", realmIds);
				query.setParameterList("roleKeys", roleNames);

				return query.list();
			}
		};
		return (List<AssignmentGradeRecord>)getHibernateTemplate().execute(hc);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.GradebookToolService#getAllCourseGradeRecords(org.sakaiproject.tool.gradebook.Gradebook)
	 * 
	 * I don't like the fact that we're passing the Gradebook object itself here (rather than an ID) since it's inconsistent with the
	 * majority of the other method signatures. However, it looks like the problem is at the level of the data model object, which stores
	 * a reference to the Gradebook object itself. We would have to modify CourseGradeRecord to solve this.
	 * 
	 */
	public List<CourseGradeRecord> getAllCourseGradeRecords(final Gradebook gradebook) {
		return (List<CourseGradeRecord>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List<CourseGradeRecord> records = getCourseGradeRecords(gradebook, session);
				return records;
			}
		});
	}

	public Assignment getAssignment(Long assignmentId) {
		return (Assignment)getHibernateTemplate().load(Assignment.class, assignmentId);
	}
	
	public boolean hasAssignments(final Long gradebookId) {
		
		Number size = (Number)getHibernateTemplate().execute(new HibernateCallback() {
			
			public Object doInHibernate(Session session) throws HibernateException {
				
				Query q = session.createQuery("select count(*) from Assignment a where a.gradebook.id=:gradebookId");
				q.setParameter("gradebookId", gradebookId);
				return (Number) q.iterate().next();
			}
		});

		return (size != null && size.intValue() == 0) ? false : true;
		
	}

	public List<AssignmentGradeRecord> getAssignmentGradeRecords(final Assignment assignment) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				if (assignment.isRemoved()) {
					return new ArrayList();                	
				}

				Query q = session.createQuery("from AssignmentGradeRecord as agr where agr.gradableObject.id=:gradableObjectId order by agr.pointsEarned");
				q.setLong("gradableObjectId", assignment.getId().longValue());

				return q.list();
			}
		};
		return (List<AssignmentGradeRecord>)getHibernateTemplate().execute(hc);
	}

	public List<AssignmentGradeRecord> getAssignmentGradeRecordsForStudent(final Long gradebookId, final String studentUid) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				if(studentUid == null) {
					if(log.isInfoEnabled()) log.info("Returning no grade records for a  null studentUid");
					return new ArrayList();
				} 

				Query q = session.createQuery("from AssignmentGradeRecord as agr where (agr.gradableObject.removed=false or agr.gradableObject.removed is null) and agr.gradableObject.gradebook.id=:gradebookId and agr.studentId=:studentUid");
				q.setLong("gradebookId", gradebookId);
				q.setString("studentUid", studentUid);

				return q.list();
			}
		};
		return (List<AssignmentGradeRecord>)getHibernateTemplate().execute(hc);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.GradebookToolService#getAssignments(java.lang.Long)
	 * 
	 * The old Gradebook1 method here used to use a default sort, but it was implemented in Java code 
	 * so didn't provide much benefit over doing the sorting ourselves in the service facade. In fact,
	 * in most cases we were probably sorting twice. I've stripped out that sorting logic. 
	 * 
	 */
	public List<Assignment> getAssignments(final Long gradebookId) {
		return (List<Assignment>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List assignments = getAssignments(gradebookId, session);

				/** end synchronize from external application*/

				// JLR - commented out as per doc above
				//sortAssignments(assignments, sortBy, ascending);
				return assignments;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.GradebookToolService#getAssignmentsForCategory(java.lang.Long)
	 * 
	 * The old Gradebook1 method excluded Assignments that had been deleted/removed. But since our new UI needs to 
	 * expose an "un-delete" operation, I've stripped that out
	 * 
	 */
	public List<Assignment> getAssignmentsForCategory(final Long categoryId) {

		if (categoryId == null)
			return null;

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				// Removed logic to ignore removed items, since we want to control this in UI
				List assignments = session.createQuery(
				"from Assignment as assign where assign.category=? order by assign.sortOrder, assign.id asc " /*and assign.removed=false*/).
				setLong(0, categoryId.longValue()).
				list();
				return assignments;
			}
		};
		return (List<Assignment>) getHibernateTemplate().execute(hc);
	}

	public List<Category> getCategories(final Long gradebookId) throws HibernateException {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				// JLR : removed logic to ignore removed, since we want to show these in UI
				List categories = session.createQuery(
				"from Category as ca where ca.gradebook.id=? order by ca.categoryOrder, ca.id asc " /*and ca.removed=false*/).
				setLong(0, gradebookId.longValue()).
				list();
				return categories;
			}
		};
		return (List<Category>) getHibernateTemplate().execute(hc);
	}
	
	public List<Category> getCategoriesWithAssignments(Long gradebookId) {

		List<Category> categories = getCategories(gradebookId);
		List<Category> categoriesWithAssignments = new ArrayList<Category>();
		if (categories != null) {
			for (Category category : categories) {

				if (category != null) {
					List<Assignment> assignments = getAssignmentsForCategory(category.getId());
					category.setAssignmentList(assignments);
					categoriesWithAssignments.add(category);
				}
			}
		}

		return categoriesWithAssignments;
	}

	public Category getCategory(final Long categoryId) throws HibernateException{
		if (categoryId == null)
			return null;

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return session.createQuery(
				"from Category as cat where cat.id=?").
				setLong(0, categoryId.longValue()).
				uniqueResult();
			}
		};
		return (Category) getHibernateTemplate().execute(hc);
	}

	public CourseGrade getCourseGrade(Long gradebookId) {
		return (CourseGrade)getHibernateTemplate().find(
				"from CourseGrade as cg where cg.gradebook.id=?",
				gradebookId).get(0);
	}

	public Gradebook getGradebook(Long id) {
		return (Gradebook)getHibernateTemplate().load(Gradebook.class, id);
	}

	public Gradebook getGradebook(String uid) throws GradebookNotFoundException {
		List list = getHibernateTemplate().find(
				"from Gradebook as gb where gb.uid=?", uid);
		if (list.size() == 1) {
			return (Gradebook) list.get(0);
		} else {
			throw new GradebookNotFoundException(
					"Could not find gradebook uid=" + uid);
		}
	}
	
	public boolean isAnyScoreEntered(final Long gradebookId, final boolean hasCategories) {
		if (log.isDebugEnabled()) log.debug("isAnyScoreEntered called for gradebookId:" + gradebookId);

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				StringBuilder query = new StringBuilder();

				query.append("select count(*) from Assignment a");

				if (hasCategories) 
					query.append(", Category cat where a.category.id = cat.id ")
					.append(" and (cat.removed = false or cat.removed is null) and ");
				else
					query.append(" where ");

				query.append(" a.gradebook.id = :gradebookId ")
				.append("and (a.removed = false or a.removed is null) ")
				.append("and a.name != 'Course Grade' ")
				.append("and a.id in ( ")
				.append("select r.gradableObject.id ")
				.append("from AssignmentGradeRecord r ")
				.append("where (r.pointsEarned is not null or r.excludedFromGrade = true) ")
				.append("and r.gradableObject.id = a.id ")
				.append(") ");

				Query q = session.createQuery(query.toString());
				q.setParameter("gradebookId", gradebookId);
				return Boolean.valueOf(((Number) q.iterate().next() ).intValue() > 0);
			}
		};

		Boolean isGraded = (Boolean)getHibernateTemplate().execute(hc);

		return isGraded != null && isGraded.booleanValue();
	}

	public boolean isStudentMissingScores(final Long gradebookId, final String studentId, final boolean hasCategories) {
		if (log.isDebugEnabled()) log.debug("isStudentMissingScores called for studentId:" + studentId);

		if (studentId == null) {
			log.debug("No student id was specified.  Returning false.");
			return false;
		}

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				StringBuilder query = new StringBuilder();

				query.append("select count(*) from Assignment a");

				if (hasCategories) 
					query.append(", Category cat where a.category.id = cat.id ")
					.append("and (cat.removed = false or cat.removed is null) ")
					.append("and (cat.extraCredit = false or cat.extraCredit is null) and ");
				else
					query.append(" where ");

				query.append(" a.gradebook.id = :gradebookId ")
				.append("and (a.removed = false or a.removed is null) ")
				.append("and a.name != 'Course Grade' ")
				.append("and (a.extraCredit = false or a.extraCredit is null) ")
				.append("and (a.countNullsAsZeros = false or a.countNullsAsZeros is null) ")
				.append("and a.id not in ( ")
				.append("select r.gradableObject.id ")
				.append("from AssignmentGradeRecord r ")
				.append("where r.studentId = :studentId ")
				.append("and (r.pointsEarned is not null or r.excludedFromGrade = true) ")
				.append("and r.gradableObject.id = a.id ")
				.append(") ");

				Query q = session.createQuery(query.toString());
				q.setParameter("gradebookId", gradebookId);
				q.setParameter("studentId", studentId);
				return Boolean.valueOf(((Number) q.iterate().next() ).intValue() > 0);
			}
		};

		Boolean isGraded = (Boolean)getHibernateTemplate().execute(hc);

		return isGraded != null && isGraded.booleanValue();
	}

	public Map<GradableObject, List<GradingEvent>> getGradingEventsForStudent(final String studentId, final Collection<GradableObject> gradableObjects) {
		if (log.isDebugEnabled()) log.debug("getGradingEventsForStudent called for studentId:" + studentId);
		Map<GradableObject, List<GradingEvent>> goEventListMap = new HashMap<GradableObject, List<GradingEvent>>();

		// Don't attempt to run the query if there are no gradableObjects or student id
		if(gradableObjects == null || gradableObjects.size() == 0) {
			log.debug("No gb items were specified.  Returning an empty GradingEvents object");
			return goEventListMap;
		}
		if (studentId == null) {
			log.debug("No student id was specified.  Returning an empty GradingEvents object");
			return goEventListMap;
		}


		for (Iterator<GradableObject> goIter = gradableObjects.iterator(); goIter.hasNext();) {
			GradableObject go = goIter.next();
			goEventListMap.put(go, new ArrayList());
		}

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List eventsList;
				if (gradableObjects.size() <= MAX_NUMBER_OF_SQL_PARAMETERS_IN_LIST) {
					Query q = session.createQuery("from GradingEvent as ge where ge.studentId=:studentId and ge.gradableObject in (:gradableObjects)");
					q.setParameterList("gradableObjects", gradableObjects, Hibernate.entity(GradableObject.class));
					q.setParameter("studentId", studentId);
					eventsList = q.list();
				} else {
					Query q = session.createQuery("from GradingEvent as ge where ge.studentId=:studentId");
					q.setParameter("studentId", studentId);
					eventsList = new ArrayList();
					for (Iterator iter = q.list().iterator(); iter.hasNext(); ) {
						GradingEvent event = (GradingEvent)iter.next();
						if (gradableObjects.contains(event.getGradableObject())) {
							eventsList.add(event);
						}
					}
				}
				return eventsList;
			}
		};

		List list = (List)getHibernateTemplate().execute(hc);

		for(Iterator iter = list.iterator(); iter.hasNext();) {
			GradingEvent event = (GradingEvent)iter.next();
			GradableObject go = event.getGradableObject();
			List<GradingEvent> goEventList = goEventListMap.get(go);
			if (goEventList != null) {
				goEventList.add(event);
				goEventListMap.put(go, goEventList);
			} 
			else {
				log.debug("event retrieved by getGradingEventsForStudent not associated with passed go list");
			}
		}

		return goEventListMap;
	}

	public List<Permission> getPermissionsForUserAnyGroup(final Long gradebookId, final String userId) throws IllegalArgumentException {
		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in BaseHibernateManager.getPermissionsForUserAnyGroup");

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery("from Permission as perm where perm.gradebookId=:gradebookId and perm.userId=:userId and perm.groupId is null");
				q.setLong("gradebookId", gradebookId);
				q.setString("userId", userId);

				return q.list();
			}
		};
		return (List<Permission>)getHibernateTemplate().execute(hc);   
	}

	public List<Permission> getPermissionsForUserForGroup(final Long gradebookId, final String userId, final List<String> groupIds) throws IllegalArgumentException {
		if (gradebookId == null || userId == null)
			throw new IllegalArgumentException(
			"Null parameter(s) in BaseHibernateManager.getPermissionsForUserForGroup");

		if (groupIds != null && groupIds.size() > 0) {
			HibernateCallback hc = new HibernateCallback() {
				public Object doInHibernate(Session session)
				throws HibernateException {
					Query q = session
					.createQuery("from Permission as perm where perm.gradebookId=:gradebookId and perm.userId=:userId and perm.groupId in (:groupIds) ");
					q.setLong("gradebookId", gradebookId);
					q.setString("userId", userId);
					q.setParameterList("groupIds", groupIds);

					return q.list();
				}
			};
			return (List<Permission>) getHibernateTemplate().execute(hc);
		} else {
			return null;
		}
	}

	public List<Comment> getComments(final Long gradebookId) {
		return (List<Comment>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List<Comment> comments;
				comments = new ArrayList<Comment>();
				Query q = session.createQuery("from Comment as c where c.gradableObject.gradebook.id=:gradebookId");
				q.setParameter("gradebookId",gradebookId);
				List allComments = q.list();
				for (Iterator iter = allComments.iterator(); iter.hasNext(); ) {
					Comment comment = (Comment)iter.next();
					comments.add(comment);
				}
				return comments;
			}
		});
	}

	public Comment getCommentForItemForStudent(final Long assignmentId, final String studentId) {
		return (Comment)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery("from Comment as c where c.studentId=:studentId and c.gradableObject.id=:assignmentId");
				q.setParameter("studentId", studentId);
				q.setParameter("assignmentId", assignmentId);
				return q.uniqueResult();
			}
		});
	}

	public List<Comment> getStudentAssignmentComments(final String studentId, final Long gradebookId) {
		return (List<Comment>)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				List<Comment> comments;
				comments = new ArrayList<Comment>();
				Query q = session.createQuery("from Comment as c where c.studentId=:studentId and c.gradableObject.gradebook.id=:gradebookId");
				q.setParameter("studentId", studentId);
				q.setParameter("gradebookId",gradebookId);
				List allComments = q.list();
				for (Iterator iter = allComments.iterator(); iter.hasNext(); ) {
					Comment comment = (Comment)iter.next();
					comments.add(comment);
				}
				return comments;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.GradebookToolService#getStudentCourseGradeRecord(org.sakaiproject.tool.gradebook.Gradebook, java.lang.String)
	 * 
	 * The old Gradebook1 service did all of its calculations from within the Hibernate code. In designing the new tool, we want to 
	 * ensure a separation of concerns between the ORM and the business logic (grade calculations). So I've stripped out the code below
	 * that does the actual grade calculation for this record. 
	 * 
	 */
	public CourseGradeRecord getStudentCourseGradeRecord(final Gradebook gradebook, final String studentId) {
		if (logData.isDebugEnabled()) logData.debug("About to read student course grade for gradebook=" + gradebook.getUid());
		return (CourseGradeRecord)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				CourseGradeRecord courseGradeRecord = getCourseGradeRecord(gradebook, studentId, session);
				if (courseGradeRecord == null) {
					courseGradeRecord = new CourseGradeRecord(getCourseGrade(gradebook.getId()), studentId);
				}

				return courseGradeRecord;
			}
		});
	}

	public Long storeActionRecord(final ActionRecord actionRecord) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				String graderId = authn.getUserUid();
				actionRecord.setDateRecorded(new Date());
				actionRecord.setGraderId(graderId);

				Long id = (Long)session.save(actionRecord);

				return id;
			}
		};

		return (Long)getHibernateTemplate().execute(hc);
	}

	public void updateAssignment(final Assignment assignment)  throws ConflictingAssignmentNameException, StaleObjectModificationException {
		
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
							
				session.evict(assignment);
				session.update(assignment);
				
				/*
				 * GRBK-913 : flushing the session so that we don't need to further evict the assignment object
				 * in case an exception occurs. In the exception handling bellow, we get the assignment from DB and compare
				 * it against the passed in assignment object
				 */
				session.flush();
				return null;
			}
		};
		
		try {
			
			getHibernateTemplate().execute(hc);	
			
		} catch (HibernateOptimisticLockingFailureException e) {
			
			// GRBK-913
			/*
			 * Getting the current assignment from the DB so that we can compare it 
			 * against the assignment instance that was supposed to be saved.
			 */
			Assignment persistedAssignment = getAssignment(assignment.getId());
			
			if(!hasEqualValues(assignment, persistedAssignment)) {
				
				if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update an assignment", e);
				throw new StaleObjectModificationException(e);
			}
			else {
				
				if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update an assignment, but the assignments have the same values", e);
			}
		}
	}

	public Set<AssignmentGradeRecord> updateAssignmentGradeRecords(final Assignment assignment, final Collection<AssignmentGradeRecord> gradeRecordsFromCall)
	throws StaleObjectModificationException {
		// If no grade records are sent, don't bother doing anything with the db
		if (gradeRecordsFromCall.size() == 0) {
			log.debug("updateAssignmentGradeRecords called for zero grade records");
			return new HashSet<AssignmentGradeRecord>();
		}

		if (logData.isDebugEnabled())
			logData.debug("BEGIN: Update " + gradeRecordsFromCall.size() + " scores for gradebook=" + assignment.getGradebook().getUid() + ", assignment=" + assignment.getName());

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Date now = new Date();
				String graderId = authn.getUserUid();

				Set<String> studentsWithUpdatedAssignmentGradeRecords = new HashSet<String>();
				Set<String> studentsWithExcessiveScores = new HashSet<String>();

				for (Iterator<AssignmentGradeRecord> iter = gradeRecordsFromCall.iterator(); iter.hasNext();) {
					AssignmentGradeRecord gradeRecordFromCall = iter.next();
					gradeRecordFromCall.setGraderId(graderId);
					gradeRecordFromCall.setDateRecorded(now);
					try {
						session.saveOrUpdate(gradeRecordFromCall);
					}
					catch (TransientObjectException e) {
						// It's possible that a previously unscored student
						// was scored behind the current user's back before
						// the user saved the new score. This translates
						// that case into an optimistic locking failure.
						if (log.isInfoEnabled())
							log.info("An optimistic locking failure occurred while attempting to add a new assignment grade record");
						throw new StaleObjectModificationException(e);
					}
					catch(DataIntegrityViolationException e) {
						
						throw new StaleObjectModificationException(e);
					}

					// Check for excessive (AKA extra credit) scoring.
					if (gradeRecordFromCall.getPointsEarned() != null && !assignment.getUngraded()
							&& gradeRecordFromCall.getPointsEarned().compareTo(assignment.getPointsPossible()) > 0) {
						studentsWithExcessiveScores.add(gradeRecordFromCall.getStudentId());
					}

					// Log the grading event, and keep track of the students
					// with saved/updated grades
					logAssignmentGradingEvent(gradeRecordFromCall, graderId, assignment, session);

					studentsWithUpdatedAssignmentGradeRecords.add(gradeRecordFromCall.getStudentId());
				}
				
				if (logData.isDebugEnabled())
					logData.debug("Updated " + studentsWithUpdatedAssignmentGradeRecords.size() + " assignment score records");

				return studentsWithExcessiveScores;
			}
		};

		Set<AssignmentGradeRecord> studentsWithExcessiveScores = (Set<AssignmentGradeRecord>) getHibernateTemplate().execute(hc);
		if (logData.isDebugEnabled())
			logData.debug("END: Update " + gradeRecordsFromCall.size() + " scores for gradebook=" + assignment.getGradebook().getUid() + ", assignment=" + assignment.getName());
		return studentsWithExcessiveScores;
	}

	public void updateCategory(final Category category) throws ConflictingCategoryNameException, StaleObjectModificationException{
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				session.evict(category);
				Category persistentCat = (Category)session.load(Category.class, category.getId());
				List conflictList = ((List)session.createQuery(
				"select ca from Category as ca where ca.name = ? and ca.gradebook = ? and ca.id != ? and (ca.removed=false or ca.removed is null)").
				setString(0, category.getName()).
				setEntity(1, category.getGradebook()).
				setLong(2, category.getId().longValue()).list());
				int numNameConflicts = conflictList.size();
				if(numNameConflicts > 0) {
					throw new ConflictingCategoryNameException("You cannot save multiple categories in a gradebook with the same name");
				}
				if(category.getWeight().doubleValue() > 1 || category.getWeight().doubleValue() < 0)
				{
					throw new IllegalArgumentException("weight for category is greater than 1 or less than 0 in updateCategory of BaseHibernateManager");
				}
				session.evict(persistentCat);
				session.update(category);
				return null;
			}
		};
		try {
			getHibernateTemplate().execute(hc);
		} catch (Exception e) {
			throw new StaleObjectModificationException(e);
		}
	}

	public void updateComment(final Comment comment) throws StaleObjectModificationException {
		final Date now = new Date();
		final String graderId = authn.getUserUid();

		// Unlike the complex grade update logic, this method assumes that
		// the client has done the work of filtering out any unchanged records
		// and isn't interested in throwing an optimistic locking exception for untouched records
		// which were changed by other sessions.
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				comment.setGraderId(graderId);
				comment.setDateRecorded(now);
				session.saveOrUpdate(comment);
				return null;
			}
		};
		try {
			getHibernateTemplate().execute(hc);
		} catch (DataIntegrityViolationException e) {
			// If a student hasn't yet received a comment for this
			// assignment, and two graders try to save a new comment record at the
			// same time, the database should report a unique constraint violation.
			// Since that's similar to the conflict between two graders who
			// are trying to update an existing comment record at the same
			// same time, this method translates the exception into an
			// optimistic locking failure.
			if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update comment");
			throw new StaleObjectModificationException(e);
		}
	}

	public void updateCourseGradeRecords(final CourseGrade courseGrade, final Collection<CourseGradeRecord> gradeRecordsFromCall) throws StaleObjectModificationException {

		if (gradeRecordsFromCall.size() == 0) {
			log.debug("updateCourseGradeRecords called with zero grade records to update");
			return;
		}

		if (logData.isDebugEnabled())
			logData.debug("BEGIN: Update " + gradeRecordsFromCall.size() + " course grades for gradebook=" + courseGrade.getGradebook().getUid());

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				for (Iterator iter = gradeRecordsFromCall.iterator(); iter.hasNext();) {
					session.evict(iter.next());
				}

				Date now = new Date();
				String graderId = authn.getUserUid();
				int numberOfUpdatedGrades = 0;

				for (Iterator iter = gradeRecordsFromCall.iterator(); iter.hasNext();) {
					// The modified course grade record
					CourseGradeRecord gradeRecordFromCall = (CourseGradeRecord) iter.next();
					gradeRecordFromCall.setGraderId(graderId);
					gradeRecordFromCall.setDateRecorded(now);
					try {
						session.saveOrUpdate(gradeRecordFromCall);
						session.flush();
					} catch (StaleObjectStateException sose) {
						if (log.isInfoEnabled())
							log.info("An optimistic locking failure occurred while attempting to update course grade records");
						throw new StaleObjectModificationException(sose);
					}

					// Log the grading event
					session.save(new GradingEvent(courseGrade, graderId, gradeRecordFromCall.getStudentId(), gradeRecordFromCall.getEnteredGrade()));

					numberOfUpdatedGrades++;
				}
				if (logData.isDebugEnabled())
					logData.debug("Changed " + numberOfUpdatedGrades + " course grades for gradebook=" + courseGrade.getGradebook().getUid());
				return null;
			}
		};
		try {
			getHibernateTemplate().execute(hc);
			if (logData.isDebugEnabled())
				logData.debug("END: Update " + gradeRecordsFromCall.size() + " course grades for gradebook=" + courseGrade.getGradebook().getUid());
		} catch (DataIntegrityViolationException e) {
			// It's possible that a previously ungraded student
			// was graded behind the current user's back before
			// the user saved the new grade. This translates
			// that case into an optimistic locking failure.
			if (log.isInfoEnabled())
				log.info("An optimistic locking failure occurred while attempting to update course grade records");
			throw new StaleObjectModificationException(e);
		}
	}

	public void updateGradebook(final Gradebook gradebook) throws StaleObjectModificationException {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				// Get the gradebook and selected mapping from persistence
				Gradebook gradebookFromPersistence = (Gradebook)session.load(
						gradebook.getClass(), gradebook.getId());
				GradeMapping mappingFromPersistence = gradebookFromPersistence.getSelectedGradeMapping();

				// If the mapping has changed, and there are explicitly entered
				// course grade records, disallow this update.
				if (!mappingFromPersistence.getId().equals(gradebook.getSelectedGradeMapping().getId())) {
					if(isExplicitlyEnteredCourseGradeRecords(gradebook.getId())) {
						throw new IllegalStateException("Selected grade mapping cannot be changed, since explicit course grades exist.");
					}
				}

				// Evict the persisted objects from the session and update the gradebook
				// so the new grade mapping is used in the sort column update
				//session.evict(mappingFromPersistence);
				for(Iterator iter = gradebookFromPersistence.getGradeMappings().iterator(); iter.hasNext();) {
					session.evict(iter.next());
				}
				session.evict(gradebookFromPersistence);
				try {
					session.update(gradebook);
					session.flush();
				} catch (StaleObjectStateException e) {
					throw new StaleObjectModificationException(e);
				}

				return null;
			}
		};
		getHibernateTemplate().execute(hc);	
	}


	public void updateUserConfiguration(final UserConfiguration userConfiguration) throws StaleObjectModificationException {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				session.saveOrUpdate(userConfiguration);
				return null;
			}
		};
		try {
			getHibernateTemplate().execute(hc);
		} catch (DataIntegrityViolationException e) {
			// If a student hasn't yet received a comment for this
			// assignment, and two graders try to save a new comment record at the
			// same time, the database should report a unique constraint violation.
			// Since that's similar to the conflict between two graders who
			// are trying to update an existing comment record at the same
			// same time, this method translates the exception into an
			// optimistic locking failure.
			if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update comment");
			throw new StaleObjectModificationException(e);
		}
	}

	public Long createPermission(Permission permission) {
		return (Long) getHibernateTemplate().save(permission);
	}

	public void deletePermission(final Long permissionId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Permission p = (Permission)session.load(Permission.class, permissionId);
				if (p != null)
					session.delete(p);
				
				return null;
			}
		};
		
		getHibernateTemplate().execute(hc);
	}

	public void deleteUserConfiguration(final String userUid, final Long gradebookId, final String configField) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				session.beginTransaction();

				Query q = session.createQuery("from UserConfiguration as config where config.userUid = :userUid and config.gradebookId = :gradebookId and config.configField = :configField ");
				q.setString("userUid", userUid);
				q.setLong("gradebookId", gradebookId.longValue());
				q.setString("configField", configField);

				UserConfiguration config = (UserConfiguration)q.uniqueResult();

				if (config != null) {
					session.delete(config);
				} 

				session.getTransaction().commit();

				return null;
			}
		};

		getHibernateTemplate().execute(hc);
	}

	public List<Permission> getPermissionsForUser(final Long gradebookId, final String userId) throws IllegalArgumentException {
		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in BaseHibernateManager.getPermissionsForUserAnyGroup");

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery("from Permission as perm where perm.gradebookId=:gradebookId and perm.userId=:userId");
				q.setLong("gradebookId", gradebookId);
				q.setString("userId", userId);

				return q.list();
			}
		};
		return (List<Permission>)getHibernateTemplate().execute(hc);   
	}

	public List<Permission> getPermissionsForUserAnyGroupForCategory(final Long gradebookId, final String userId, final List<Long> cateIds) throws IllegalArgumentException {

		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in BaseHibernateManager.getPermissionsForUserAnyGroupForCategory");

		if(cateIds != null && cateIds.size() > 0)
		{
			HibernateCallback hc = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query q = session.createQuery("from Permission as perm where perm.gradebookId=:gradebookId and perm.userId=:userId and perm.categoryId in (:cateIds) and perm.groupId is null");
					q.setLong("gradebookId", gradebookId);
					q.setString("userId", userId);
					q.setParameterList("cateIds", cateIds);

					return q.list();
				}
			};
			return (List)getHibernateTemplate().execute(hc);
		}
		else
		{
			return null;
		}    	
	}

	public List getPermissionsForUserAnyGroupAnyCategory(final Long gradebookId, final String userId) throws IllegalArgumentException {

		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in BaseHibernateManager.getPermissionsForUserAnyGroupForCategory");

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery("from Permission as perm where perm.gradebookId=:gradebookId and perm.userId=:userId and perm.categoryId is null and perm.groupId is null");
				q.setLong("gradebookId", gradebookId);
				q.setString("userId", userId);

				return q.list();
			}
		};
		return (List)getHibernateTemplate().execute(hc);
	}

	public List<Permission> getPermissionForUserAnyCategory(final Long gradebookId, final String userId) throws IllegalArgumentException {

		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in BaseHibernateManager.getPermissionsForUserAnyGroupForCategory");

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery("from Permission as perm where perm.gradebookId=:gradebookId and perm.userId=:userId and perm.categoryId is null");
				q.setLong("gradebookId", gradebookId);
				q.setString("userId", userId);

				return q.list();
			}
		};
		return (List)getHibernateTemplate().execute(hc);
	}

	public List<Permission> getPermissionsForUserForGoupsAnyCategory(final Long gradebookId, final String userId, final List<String> groupIds) throws IllegalArgumentException {

		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in BaseHibernateManager.getPermissionsForUserForGoupsAnyCategory");

		if (groupIds != null && groupIds.size() > 0) {
			HibernateCallback hc = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query q = session.createQuery("from Permission as perm where perm.gradebookId=:gradebookId and perm.userId=:userId and perm.categoryId is null and perm.groupId in (:groupIds) ");
					q.setLong("gradebookId", gradebookId);
					q.setString("userId", userId);
					q.setParameterList("groupIds", groupIds);

					return q.list();
				}
			};
			return (List)getHibernateTemplate().execute(hc);
		} else {
			return null;
		}
	}

	public List<Permission> getPermissionsForUserForCategory(final Long gradebookId, final String userId, final List<Long> cateIds) throws IllegalArgumentException {

		if(gradebookId == null || userId == null)
			throw new IllegalArgumentException("Null parameter(s) in BaseHibernateManager.getPermissionsForUserForCategory");

		if(cateIds != null && cateIds.size() > 0)
		{
			HibernateCallback hc = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query q = session.createQuery("from Permission as perm where perm.gradebookId=:gradebookId and perm.userId=:userId and perm.categoryId in (:cateIds)");
					q.setLong("gradebookId", gradebookId);
					q.setString("userId", userId);
					q.setParameterList("cateIds", cateIds);

					return q.list();
				}
			};
			return (List)getHibernateTemplate().execute(hc);
		}
		else
		{
			return null;
		}
	}

	/*
	 * HELPER METHODS
	 * 
	 */

	protected Double getDoublePointForRecord(AssignmentGradeRecord gradeRecordFromCall)
	{
		Assignment assign = getAssignment(gradeRecordFromCall.getAssignment().getId()); 
		return gradeCalculations.calculateDoublePointForRecord(assign, gradeRecordFromCall);
	}

	/**
	 * Oracle has a low limit on the maximum length of a parameter list
	 * in SQL queries of the form "WHERE tbl.col IN (:paramList)".
	 * Since enrollment lists can sometimes be very long, we've replaced
	 * such queries with full selects followed by filtering. This helper
	 * method filters out unwanted grade records. (Typically they're not
	 * wanted because they're either no longer officially enrolled in the
	 * course or they're not members of the selected section.)
	 */
	protected List<AbstractGradeRecord> filterGradeRecordsByStudents(Collection<AbstractGradeRecord> gradeRecords, Collection<String> studentUids) {
		List<AbstractGradeRecord> filteredRecords = new ArrayList<AbstractGradeRecord>();
		for (AbstractGradeRecord agr : gradeRecords) {
			if (studentUids.contains(agr.getStudentId())) {
				filteredRecords.add(agr);
			}
		}
		return filteredRecords;
	}

	protected Set<String> getAllStudentUids(String gradebookUid) {
		List enrollments = getSectionAwareness().getSiteMembersInRole(gradebookUid, Role.STUDENT);
		Set<String> studentUids = new HashSet<String>();
		for(Iterator iter = enrollments.iterator(); iter.hasNext();) {
			studentUids.add(((EnrollmentRecord)iter.next()).getUser().getUserUid());
		}
		return studentUids;
	}

	protected List<Assignment> getAssignments(Long gradebookId, Session session) throws HibernateException {
		List<Assignment> assignments = session.createQuery(
		"from Assignment as asn where asn.gradebook.id=? and (asn.removed=false or asn.removed is null) order by asn.sortOrder, asn.id asc").
		setLong(0, gradebookId.longValue()).
		list();
		return assignments;
	}

	/**
	 * Gets the course grade record for a student, or null if it does not yet exist.
	 *
	 * @param studentId The student ID
	 * @param session The hibernate session
	 * @return A List of grade records
	 *
	 * @throws HibernateException
	 */
	protected CourseGradeRecord getCourseGradeRecord(Gradebook gradebook, String studentId, Session session) throws HibernateException {
		return (CourseGradeRecord)session.createQuery(
		"from CourseGradeRecord as cgr where cgr.studentId=? and cgr.gradableObject.gradebook=?").
		setString(0, studentId).
		setEntity(1, gradebook).
		uniqueResult();
	}

	protected List<CourseGradeRecord> getCourseGradeRecords(Gradebook gradebook, Session session) {
		return (List<CourseGradeRecord>)session.createQuery(
		"from CourseGradeRecord as cgr where cgr.gradableObject.gradebook=?").
		setEntity(0, gradebook).list();	
	}

	protected LetterGradePercentMapping getDefaultLetterGradePercentMapping() {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session)
			throws HibernateException {
				List defaultMapping = (session
						.createQuery("select lgpm from LetterGradePercentMapping as lgpm where lgpm.mappingType = 1"))
						.list();
				if (defaultMapping == null || defaultMapping.size() == 0) {
					log
					.info("Default letter grade mapping hasn't been created in DB in BaseHibernateManager.getDefaultLetterGradePercentMapping");
					return null;
				}
				if (defaultMapping.size() > 1) {
					log
					.error("Duplicate default letter grade mapping was created in DB in BaseHibernateManager.getDefaultLetterGradePercentMapping");
					return null;
				}

				return ((LetterGradePercentMapping) defaultMapping.get(0));

			}
		};

		return (LetterGradePercentMapping) getHibernateTemplate().execute(hc);
	}

	public String getGradebookUid(Long id) {
		return ((Gradebook)getHibernateTemplate().load(Gradebook.class, id)).getUid();
	}

	public GradeMapping getGradeMapping(final Long id) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				GradeMapping mapping = (GradeMapping)session.load(GradeMapping.class, id);

				return mapping;
			}
		};

		return (GradeMapping)getHibernateTemplate().execute(hc);
	}

	public Set<GradeMapping> getGradeMappings(final Long id) {

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Gradebook gradebook = (Gradebook)session.load(Gradebook.class, id);

				if (gradebook == null)
					return null;

				Set<GradeMapping> mappings = gradebook.getGradeMappings();

				// Loop through each one in order to ensure that these are fetched in session
				for (GradeMapping mapping : mappings) {

				}

				return mappings;
			}
		};

		return (Set<GradeMapping>)getHibernateTemplate().execute(hc);
	}

	protected LetterGradePercentMapping getLetterGradePercentMapping(
			final Gradebook gradebook) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session)
			throws HibernateException {
				LetterGradePercentMapping mapping = (LetterGradePercentMapping) ((session
						.createQuery("from LetterGradePercentMapping as lgpm where lgpm.gradebookId=:gradebookId and lgpm.mappingType=2"))
						.setLong("gradebookId", gradebook.getId().longValue()))
						.uniqueResult();
				if (mapping == null) {
					LetterGradePercentMapping lgpm = getDefaultLetterGradePercentMapping();
					LetterGradePercentMapping returnLgpm = new LetterGradePercentMapping();
					returnLgpm.setGradebookId(gradebook.getId());
					returnLgpm.setGradeMap(lgpm.getGradeMap());
					returnLgpm.setMappingType(2);
					return returnLgpm;
				}
				return mapping;

			}
		};

		return (LetterGradePercentMapping) getHibernateTemplate().execute(hc);
	}

	/**
	 * this method is different with getLetterGradePercentMapping - it returns
	 * null if no mapping exists for gradebook instead of returning default
	 * mapping.
	 */
	protected LetterGradePercentMapping getLetterGradePercentMappingForGradebook(
			final Gradebook gradebook) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session)
			throws HibernateException {
				LetterGradePercentMapping mapping = (LetterGradePercentMapping) ((session
						.createQuery("from LetterGradePercentMapping as lgpm where lgpm.gradebookId=:gradebookId and lgpm.mappingType=2"))
						.setLong("gradebookId", gradebook.getId().longValue()))
						.uniqueResult();
				return mapping;

			}
		};

		return (LetterGradePercentMapping) getHibernateTemplate().execute(hc);
	}

	protected boolean isExplicitlyEnteredCourseGradeRecords(final Long gradebookId) {
		final Set<String> studentUids = getAllStudentUids(getGradebookUid(gradebookId));
		if (studentUids.isEmpty()) {
			return false;
		}

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Integer total;
				if (studentUids.size() <= MAX_NUMBER_OF_SQL_PARAMETERS_IN_LIST) {
					Query q = session.createQuery(
					"select cgr from CourseGradeRecord as cgr where cgr.enteredGrade is not null and cgr.gradableObject.gradebook.id=:gradebookId and cgr.studentId in (:studentUids)");
					q.setLong("gradebookId", gradebookId.longValue());
					q.setParameterList("studentUids", studentUids);
					List totalList = (List)q.list();
					total = Integer.valueOf(totalList.size());
					if (log.isInfoEnabled()) log.info("total number of explicitly entered course grade records = " + total);
				} else {
					total = Integer.valueOf(0);
					Query q = session.createQuery(
					"select cgr.studentId from CourseGradeRecord as cgr where cgr.enteredGrade is not null and cgr.gradableObject.gradebook.id=:gradebookId");
					q.setLong("gradebookId", gradebookId.longValue());
					for (Iterator iter = q.list().iterator(); iter.hasNext(); ) {
						String studentId = (String)iter.next();
						if (studentUids.contains(studentId)) {
							total = Integer.valueOf(1);
							break;
						}
					}
				}
				return total;
			}
		};
		return ((Number)getHibernateTemplate().execute(hc)).intValue() > 0;
	}

	protected void logAssignmentGradingEvent(AssignmentGradeRecord gradeRecord, String graderId, Assignment assignment, Session session) {
		if (gradeRecord == null || assignment == null) {
			throw new IllegalArgumentException("null gradeRecord or assignment passed to logAssignmentGradingEvent");
		}

		// Log the grading event, and keep track of the students with saved/updated grades
		// we need to log what the user entered depending on the grade entry type
		Gradebook gradebook = assignment.getGradebook();
		String gradeEntry = null;
		if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_LETTER) {
			gradeEntry = gradeRecord.getLetterEarned();
		} else if (gradebook.getGrade_type() == GradebookService.GRADE_TYPE_PERCENTAGE) {
			if (gradeRecord.getPercentEarned() != null)
				gradeEntry = gradeRecord.getPercentEarned().toString();
		} else {
			if (gradeRecord.getPointsEarned() != null)
				gradeEntry = gradeRecord.getPointsEarned().toString();
		}

		if (gradeRecord.isExcludedFromGrade() != null && gradeRecord.isExcludedFromGrade().booleanValue())
			gradeEntry = "excused";

		session.save(new GradingEvent(assignment, graderId, gradeRecord.getStudentId(), gradeEntry));
	}

	protected void updateLetterGradePercentMapping(final Map<String, Double> gradeMap, final Gradebook gradebook) {
		HibernateCallback hcb = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				LetterGradePercentMapping lgpm = getLetterGradePercentMapping(gradebook);

				if (lgpm == null)
					throw new IllegalArgumentException("LetterGradePercentMapping is null in BaseHibernateManager.updateLetterGradePercentMapping");

				if (gradeMap == null)
					throw new IllegalArgumentException("gradeMap is null in BaseHibernateManager.updateLetterGradePercentMapping");

				Map<String, Double> saveMap = new HashMap<String, Double>();
				for (Iterator<String> iter = gradeMap.keySet().iterator(); iter.hasNext();) {
					String key = iter.next();
					saveMap.put(key, gradeMap.get(key));
				}

				lgpm.setGradeMap(saveMap);
				session.save(lgpm);

				return null;
			}
		};
		getHibernateTemplate().execute(hcb);
	}

	protected boolean validateLetterGradeMapping(Map<String, Double> gradeMap) {
		Set<String> keySet = gradeMap.keySet();

		for(Iterator<String> iter = keySet.iterator(); iter.hasNext(); )
		{
			String key = iter.next();
			boolean validLetter = false;
			for(int i=0; i<GradebookService.validLetterGrade.length; i++)
			{
				if(key.equalsIgnoreCase(GradebookService.validLetterGrade[i]))
				{
					validLetter = true;
					break;
				}
			}
			if(validLetter == false)
				return false;
		}
		return true;
	}

	private boolean hasEqualValues(Assignment a1, Assignment a2) {
		
		if(null == a1 || null == a2) {
			return false;
		}
		else if(null != a1.isExtraCredit() && null == a2.isExtraCredit()) {
			return false;
		}
		else if(null == a1.isExtraCredit() && null != a2.isExtraCredit()) {
			return false;
		}
		else if(null != a1.isExtraCredit() && null != a2.isExtraCredit() && !a1.isExtraCredit().equals(a2.isExtraCredit())) {
			return false;
		}
		else if(a1.isReleased() != a2.isReleased()) {
			return false;
		}
		else if(null != a1.getPointsPossible() && null == a2.getPointsPossible()) {
			return false;
		}
		else if(null == a1.getPointsPossible() && null != a2.getPointsPossible()) {
			return false;
		}
		else if(null != a1.getPointsPossible() && null != a2.getPointsPossible() && !a1.getPointsPossible().equals(a2.getPointsPossible())) {
			return false;
		}
		else if(null != a1.getDueDate() && null == a2.getDueDate()) {
			return false;
		}
		else if(null == a1.getDueDate() && null != a2.getDueDate()) {
			return false;
		}
		else if(null != a1.getDueDate() && null != a2.getDueDate() && !a1.getDueDate().equals(a2.getDueDate())) {
			return false;
		}
		else if(a1.isRemoved() != a2.isRemoved()) {
			return false;
		}
		else if(a1.isNotCounted() != a2.isNotCounted()) {
			return false;
		}
		else if(null != a1.getSortOrder() && null == a2.getSortOrder()) {
			return false;
		}
		else if(null == a1.getSortOrder() && null != a2.getSortOrder()) {
			return false;
		}
		else if(null != a1.getSortOrder() && null != a2.getSortOrder() && !a1.getSortOrder().equals(a2.getSortOrder())) {
			return false;
		}
		else if(null != a1.getCountNullsAsZeros() && null == a2.getCountNullsAsZeros()) {
			return false;
		}
		else if(null == a1.getCountNullsAsZeros() && null != a2.getCountNullsAsZeros()) {
			return false;
		}
		else if(null != a1.getCountNullsAsZeros() && null != a2.getCountNullsAsZeros() && !a1.getCountNullsAsZeros().equals(a2.getCountNullsAsZeros())) {
			return false;
		}
		else if(null != a1.getAssignmentWeighting() && null == a2.getAssignmentWeighting()) {
			return false;
		}
		else if(null == a1.getAssignmentWeighting() && null != a2.getAssignmentWeighting()) {
			return false;
		}
		else if(null != a1.getAssignmentWeighting() && null != a2.getAssignmentWeighting() && !a1.getAssignmentWeighting().equals(a2.getAssignmentWeighting())) {
			return false;
		}
		else if(null != a1.getCategory() && null == a2.getCategory()) {
			return false;
		}
		else if(null == a1.getCategory() && null != a2.getCategory()) {
			return false;
		}
		else if(null != a1.getCategory() && null != a2.getCategory() && !a1.getCategory().getId().equals(a2.getCategory().getId())) {
			return false;
		}
		
		return true;
	}

	/*
	 * DEPENDENCY INJECTION ACCESSORS
	 * 
	 */
	
	public void setGradeCalculations(GradeCalculations gradeCalculations) {
		this.gradeCalculations = gradeCalculations;
	}

	public SectionAwareness getSectionAwareness() {
		return sectionAwareness;
	}

	public void setSectionAwareness(SectionAwareness sectionAwareness) {
		this.sectionAwareness = sectionAwareness;
	}

	public Gradebook2Authn getAuthn() {
		return authn;
	}

	public void setAuthn(Gradebook2Authn authn) {
		this.authn = authn;
	}

	public EventTrackingService getEventTrackingService() {
		return eventTrackingService;
	}

	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}

	public Boolean hasGradeOverrides(Long gradebookId) {
		
		return Boolean.valueOf(isExplicitlyEnteredCourseGradeRecords(gradebookId));
	}

}
