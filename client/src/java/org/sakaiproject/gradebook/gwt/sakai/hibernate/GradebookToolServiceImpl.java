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

import java.math.BigDecimal;
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
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.TransientObjectException;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.model.ActionRecord;
import org.sakaiproject.section.api.SectionAwareness;
import org.sakaiproject.section.api.coursemanagement.EnrollmentRecord;
import org.sakaiproject.section.api.facade.Role;
import org.sakaiproject.service.gradebook.shared.ConflictingAssignmentNameException;
import org.sakaiproject.service.gradebook.shared.ConflictingCategoryNameException;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.service.gradebook.shared.StaleObjectModificationException;
import org.sakaiproject.thread_local.cover.ThreadLocalManager;
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
import org.sakaiproject.tool.gradebook.business.GbSynchronizer;
import org.sakaiproject.tool.gradebook.facades.Authn;
import org.sakaiproject.tool.gradebook.facades.EventTrackingService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * This is the Hibernate implementation of the GradebookToolService for Gradebook2/GradebookNG. At the time of this
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
    protected Authn authn;
    protected EventTrackingService eventTrackingService;
	
    // Local cache of static-between-deployment properties.
    protected Map propertiesMap = new HashMap();
    
	/** synchronize from external application*/
    GbSynchronizer synchronizer = null;
	
    /*
	 * GRADEBOOKTOOLSERVICE IMPLEMENTATION
	 * 
	 */
    
	public Long createAssignmentForCategory(final Long gradebookId, final Long categoryId,
			final String name, final Double points, final Date dueDate, final Boolean isNotCounted,
			final Boolean isReleased) throws ConflictingAssignmentNameException, StaleObjectModificationException, IllegalArgumentException
	    {
	    	if(gradebookId == null || categoryId == null)
	    	{
	    		throw new IllegalArgumentException("gradebookId or categoryId is null in BaseHibernateManager.createAssignmentForCategory");
	    	}

	    	HibernateCallback hc = new HibernateCallback() {
	    		public Object doInHibernate(Session session) throws HibernateException {
	    			Gradebook gb = (Gradebook)session.load(Gradebook.class, gradebookId);
	    			Category cat = (Category)session.load(Category.class, categoryId);
	    			List conflictList = ((List)session.createQuery(
	    			"select go from GradableObject as go where go.name = ? and go.gradebook = ? and go.removed=false").
	    			setString(0, name).
	    			setEntity(1, gb).list());
	    			int numNameConflicts = conflictList.size();
	    			if(numNameConflicts > 0) {
	    				throw new ConflictingAssignmentNameException("You can not save multiple assignments in a gradebook with the same name");
	    			}

	    			Assignment asn = new Assignment();
	    			asn.setGradebook(gb);
	    			asn.setCategory(cat);
	    			asn.setName(name.trim());
	    			asn.setPointsPossible(points);
	    			asn.setDueDate(dueDate);
	    			asn.setUngraded(false);
	    			if (isNotCounted != null) {
	    				asn.setNotCounted(isNotCounted.booleanValue());
	    			}

	    			if(isReleased!=null){
	    				asn.setReleased(isReleased.booleanValue());
	    			}

	    			/** synchronize from external application */
	    			if (synchronizer != null && !synchronizer.isProjectSite())
	    			{
	    				synchronizer.addLegacyAssignment(name);
	    			}  

	    			Long id = (Long)session.save(asn);

	    			return id;
	    		}
	    	};

	    	return (Long)getHibernateTemplate().execute(hc);
	    }

	public Long createCategory(final Long gradebookId, final String name, final Double weight, final int drop_lowest) 
    throws ConflictingCategoryNameException, StaleObjectModificationException {
    	HibernateCallback hc = new HibernateCallback() {
    		public Object doInHibernate(Session session) throws HibernateException {
    			Gradebook gb = (Gradebook)session.load(Gradebook.class, gradebookId);
    			List conflictList = ((List)session.createQuery(
    					"select ca from Category as ca where ca.name = ? and ca.gradebook = ? and ca.removed=false ").
    					setString(0, name).
    					setEntity(1, gb).list());
    			int numNameConflicts = conflictList.size();
    			if(numNameConflicts > 0) {
    				throw new ConflictingCategoryNameException("You can not save multiple catetories in a gradebook with the same name");
    			}
    			if(weight > 1 || weight < 0)
    			{
    				throw new IllegalArgumentException("weight for category is greater than 1 or less than 0 in createCategory of BaseHibernateManager");
    			}

    			Category ca = new Category();
    			ca.setGradebook(gb);
    			ca.setName(name);
    			ca.setWeight(weight);
    			ca.setDrop_lowest(drop_lowest);
    			ca.setRemoved(false);

    			Long id = (Long)session.save(ca);

    			return id;
    		}
    	};

    	return (Long)getHibernateTemplate().execute(hc);
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
		Integer size = (Integer)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query q = session.createQuery("select count(*) from ActionRecord as ar where ar.gradebookUid=:gradebookUid ");
                q.setString("gradebookUid", gradebookUid);
                return (Integer) q.iterate().next();
            }
        });
		
		return size;
	}

	public List<AssignmentGradeRecord> getAllAssignmentGradeRecords(final Long gradebookId, final Collection<String> studentUids) {
		HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                if(studentUids.size() == 0) {
                    // If there are no enrollments, no need to execute the query.
                    if(log.isInfoEnabled()) log.info("No enrollments were specified.  Returning an empty List of grade records");
                    return new ArrayList();
                } else {
                    Query q = session.createQuery("from AssignmentGradeRecord as agr where agr.gradableObject.removed=false and " +
                            "agr.gradableObject.gradebook.id=:gradebookId order by agr.pointsEarned");
                    q.setLong("gradebookId", gradebookId.longValue());
                    return filterGradeRecordsByStudents(q.list(), studentUids);
                }
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

	public AssignmentGradeRecord getAssignmentGradeRecordForAssignmentForStudent(final Assignment assignment, final String studentUid) {
	    HibernateCallback hc = new HibernateCallback() {
	        public Object doInHibernate(Session session) throws HibernateException {
	            if(studentUid == null) {
	                if(log.isInfoEnabled()) log.info("Returning no grade records for a null student UID");
	                return new ArrayList();
	            } else if (assignment.isRemoved()) {
	                return new ArrayList();                	
	            }
	
	            Query q = session.createQuery("from AssignmentGradeRecord as agr where agr.gradableObject.id=:gradableObjectId " +
	            		"and agr.studentId=:student");
	            q.setLong("gradableObjectId", assignment.getId().longValue());
	            q.setString("student", studentUid);
	            return q.list();
	        }
	    };
	    List results = (List) getHibernateTemplate().execute(hc);
	    if (results.size() > 0){
	    	return (AssignmentGradeRecord)results.get(0);
	    } else {
	    	return new AssignmentGradeRecord();
	    }
	}

	public List<AssignmentGradeRecord> getAssignmentGradeRecords(final Assignment assignment, final Collection<String> studentUids) {
		 HibernateCallback hc = new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException {
	                if(studentUids == null || studentUids.size() == 0) {
	                    if(log.isInfoEnabled()) log.info("Returning no grade records for an empty collection of student UIDs");
	                    return new ArrayList();
	                } else if (assignment.isRemoved()) {
	                    return new ArrayList();                	
	                }

	                Query q = session.createQuery("from AssignmentGradeRecord as agr where agr.gradableObject.id=:gradableObjectId order by agr.pointsEarned");
	                q.setLong("gradableObjectId", assignment.getId().longValue());
	                List records = filterGradeRecordsByStudents(q.list(), studentUids);
	                return records;
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
                
                /** synchronize from external application*/
                if (synchronizer != null)
                {
                	synchronizer.synchrornizeAssignments(assignments);

                    assignments = getAssignments(gradebookId, session);
                }
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
		HibernateCallback hc = new HibernateCallback() {
    		public Object doInHibernate(Session session) throws HibernateException {
    			// Removed logic to ignore removed items, since we want to control this in UI
    			List assignments = session.createQuery(
					"from Assignment as assign where assign.category=? " /*and assign.removed=false*/).
					setLong(0, categoryId.longValue()).
					list();
    			return assignments;
    		}
    	};
    	return (List<Assignment>) getHibernateTemplate().execute(hc);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.sakai.GradebookToolService#getAssignmentsWithNoCategory(java.lang.Long, java.lang.String, boolean)
	 * 
	 * As with getAssignments above, I've decided to strip out the sorting from this method. The only difference is that in this case
	 * we actually get a different method signature, since it would be misleading, I think, to pass arguments we don't use.
	 * 
	 */
	public List<Assignment> getAssignmentsWithNoCategory(final Long gradebookId /*, String assignmentSort, boolean assignAscending*/) {
		
		HibernateCallback hc = new HibernateCallback() {
    		public Object doInHibernate(Session session) throws HibernateException {
    			List assignments = session.createQuery(
    					"from Assignment as asn where asn.gradebook.id=? and asn.removed=false and asn.category is null").
    					setLong(0, gradebookId.longValue()).
    					list();
    			return assignments;
    		}
    	};
    	
    	List<Assignment> assignList = (List)getHibernateTemplate().execute(hc);
    	/*if(assignmentSort != null)
    		sortAssignments(assignList, assignmentSort, assignAscending);
    	else
    		sortAssignments(assignList, Assignment.DEFAULT_SORT, assignAscending);
    	*/
    	return assignList;
	}

	public List<Category> getCategories(final Long gradebookId) throws HibernateException {
    	HibernateCallback hc = new HibernateCallback() {
    		public Object doInHibernate(Session session) throws HibernateException {
    			// JLR : removed logic to ignore removed, since we want to show these in UI
    			List categories = session.createQuery(
					"from Category as ca where ca.gradebook=? " /*and ca.removed=false*/).
					setLong(0, gradebookId.longValue()).
					list();
    			return categories;
    		}
    	};
    	return (List<Category>) getHibernateTemplate().execute(hc);
	}

	public Category getCategory(final Long categoryId) throws HibernateException{
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
	
	public boolean isStudentCommented(final String studentId, final Long assignmentId) {
		Boolean isCommented = (Boolean)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query q = session.createQuery("select count(*) from Comment as c where c.studentId=:studentId and c.gradableObject.id=:assignmentId");
                q.setParameter("studentId", studentId);
                q.setParameter("assignmentId",assignmentId);
                return Boolean.valueOf(((Integer) q.iterate().next() ).intValue() > 0);
            }
        });
		
		return isCommented != null && isCommented.booleanValue();
	}
	
	public boolean isStudentGraded(final String studentId) {
		if (log.isDebugEnabled()) log.debug("isStudentGraded called for studentId:" + studentId);
    	
        if (studentId == null) {
        	log.debug("No student id was specified.  Returning false.");
        	return false;
        }

        HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

               Query q = session.createQuery("select count(*) from GradingEvent as ge where ge.studentId=:studentId and ge.gradableObject.removed=false");
               q.setParameter("studentId", studentId);
               return Boolean.valueOf(((Integer) q.iterate().next() ).intValue() > 0);
               
            }
        };

        Boolean isGraded = (Boolean)getHibernateTemplate().execute(hc);

        return isGraded != null && isGraded.booleanValue();
	}
	
	public boolean isStudentGraded(final String studentId, final Long assignmentId) {
		if (log.isDebugEnabled()) log.debug("isStudentGraded called for studentId:" + studentId);
    	
        // Don't attempt to run the query if there are no gradableObjects or student id
        if (assignmentId == null) {
            log.debug("No assignment specified.  Returning false.");
            return false;
        }
        if (studentId == null) {
        	log.debug("No student id was specified.  Returning false.");
        	return false;
        }

        HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

                Query q = session.createQuery("select count(*) from GradingEvent as ge where ge.studentId=:studentId and ge.gradableObject.id=:assignmentId");
                q.setParameter("assignmentId", assignmentId);
                q.setParameter("studentId", studentId);
                return Boolean.valueOf(((Integer) q.iterate().next() ).intValue() > 0);
            
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
        
        
        for (Iterator goIter = gradableObjects.iterator(); goIter.hasNext();) {
        	GradableObject go = (GradableObject) goIter.next();
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
            List goEventList = (List) goEventListMap.get(go);
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
    	return (List)getHibernateTemplate().execute(hc);   
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
			return (List) getHibernateTemplate().execute(hc);
		} else {
			return null;
		}
	}
	
	public List<Comment> getComments(final Long gradebookId) {
		return (List<Comment>)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                List comments;
                comments = new ArrayList();
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

	public List<Comment> getStudentAssignmentComments(final String studentId, final Long gradebookId) {
		return (List<Comment>)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                List comments;
                comments = new ArrayList();
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
                
                // JLR - commented out as per doc above
                /*
                // Only take the hit of autocalculating the course grade if no explicit
                // grade has been entered.
                if (courseGradeRecord.getEnteredGrade() == null) {
                    // We could easily get everything we need in a single query by using an outer join if we
                    // weren't mapping the different classes together into single sparsely populated
                    // tables. When we finally break up the current mungings of Assignment with CourseGrade
                    // and AssignmentGradeRecord with CourseGradeRecord, redo this section.
                	List cates = getCategories(gradebook.getId());
                	//double totalPointsPossible = getTotalPointsInternal(gradebook.getId(), session);
                	//double totalPointsEarned = getTotalPointsEarnedInternal(gradebook.getId(), studentId, session);
                	double totalPointsPossible = getTotalPointsInternal(gradebook.getId(), session, gradebook, cates, studentId);
                	List totalEarned = getTotalPointsEarnedInternal(gradebook.getId(), studentId, session, gradebook, cates);
                	double totalPointsEarned = ((Double)totalEarned.get(0)).doubleValue();
                	double literalTotalPointsEarned = ((Double)totalEarned.get(1)).doubleValue();
                	courseGradeRecord.initNonpersistentFields(totalPointsPossible, totalPointsEarned, literalTotalPointsEarned);
                } */            
                return courseGradeRecord;
            }
        });
	}

	public void saveOrUpdateLetterGradePercentMapping(final Map<String, Double> gradeMap, final Gradebook gradebook) {
		if(gradeMap == null)
    		throw new IllegalArgumentException("gradeMap is null in BaseHibernateManager.saveOrUpdateLetterGradePercentMapping");

    	LetterGradePercentMapping lgpm = getLetterGradePercentMappingForGradebook(gradebook);

    	if(lgpm == null)
    	{
    		Set keySet = gradeMap.keySet();

    		if(keySet.size() != GradebookService.validLetterGrade.length) //we only consider letter grade with -/+ now.
    			throw new IllegalArgumentException("gradeMap doesn't have right size in BaseHibernateManager.saveOrUpdateLetterGradePercentMapping");

    		if(validateLetterGradeMapping(gradeMap) == false)
    			throw new IllegalArgumentException("gradeMap contains invalid letter in BaseHibernateManager.saveOrUpdateLetterGradePercentMapping");

    		HibernateCallback hcb = new HibernateCallback()
    		{
    			public Object doInHibernate(Session session) throws HibernateException,
    			SQLException
    			{
    				LetterGradePercentMapping lgpm = new LetterGradePercentMapping();
    				if (lgpm != null)
    				{                    
    					Map saveMap = new HashMap();
    					for (Iterator gradeIter = gradeMap.keySet().iterator(); gradeIter.hasNext();) {
    						String letterGrade = (String)gradeIter.next();
    						Double value = (Double)gradeMap.get(letterGrade);
    						saveMap.put(letterGrade, value);
    					}
    					
    					lgpm.setGradeMap(saveMap);
    					lgpm.setGradebookId(gradebook.getId());
    					lgpm.setMappingType(2);
    					session.save(lgpm);
    				}
    				return null;
    			}
    		}; 
    		getHibernateTemplate().execute(hcb);
    	}
    	else
    	{
    		updateLetterGradePercentMapping(gradeMap, gradebook);
    	}	
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
            	updateAssignment(assignment, session);
                return null;
            }
        };
        try {
        	/** synchronize from external application*/
        	String oldTitle = null;
        	if(synchronizer != null)
        	{
        		Assignment assign = getAssignment(assignment.getId());
        		oldTitle = assign.getName();
        	}
            getHibernateTemplate().execute(hc);
        	/** synchronize from external application*/
        	if(synchronizer != null && oldTitle != null  && !synchronizer.isProjectSite())
        	{
        		synchronizer.updateAssignment(oldTitle, assignment.getName());
        	}
        } catch (HibernateOptimisticLockingFailureException holfe) {
            if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update an assignment");
            throw new StaleObjectModificationException(holfe);
        }
	}

	public Set<AssignmentGradeRecord> updateAssignmentGradeRecords(final Assignment assignment, final Collection<AssignmentGradeRecord> gradeRecordsFromCall)
			throws StaleObjectModificationException {
		// If no grade records are sent, don't bother doing anything with the db
		if (gradeRecordsFromCall.size() == 0) {
			log.debug("updateAssignmentGradeRecords called for zero grade records");
			return new HashSet();
		}

		if (logData.isDebugEnabled())
			logData.debug("BEGIN: Update " + gradeRecordsFromCall.size() + " scores for gradebook=" + assignment.getGradebook().getUid() + ", assignment=" + assignment.getName());

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Date now = new Date();
				String graderId = authn.getUserUid();

				Set studentsWithUpdatedAssignmentGradeRecords = new HashSet();
				Set studentsWithExcessiveScores = new HashSet();

				/** synchronize from external application */
				if (synchronizer != null) {
					boolean isUpdateAll = Boolean.TRUE.equals(ThreadLocalManager.get("iquiz_update_all"));
					boolean isIquizCall = Boolean.TRUE.equals(ThreadLocalManager.get("iquiz_call"));
					boolean isStudentView = Boolean.TRUE.equals(ThreadLocalManager.get("iquiz_student_view"));

					Map iquizAssignmentMap = new HashMap();
					List legacyUpdates = new ArrayList();
					Map convertedEidUidRecordMap = new HashMap();

					convertedEidUidRecordMap = synchronizer.convertEidUid(gradeRecordsFromCall);
					if (!isUpdateAll && synchronizer != null && !synchronizer.isProjectSite()) {
						iquizAssignmentMap = synchronizer.getLegacyAssignmentWithStats(assignment.getName());
					}
					Map recordsFromCLDb = null;
					if (synchronizer != null && isIquizCall && isUpdateAll) {
						recordsFromCLDb = synchronizer.getPersistentRecords(assignment.getId());
					}

					for (Iterator iter = gradeRecordsFromCall.iterator(); iter.hasNext();) {
						AssignmentGradeRecord gradeRecordFromCall = (AssignmentGradeRecord) iter.next();

						boolean updated = false;
						if (isIquizCall && synchronizer != null) {
							gradeRecordFromCall = synchronizer.convertIquizRecordToUid(gradeRecordFromCall, convertedEidUidRecordMap, isUpdateAll, graderId);
						} else {
							gradeRecordFromCall.setGraderId(graderId);
							gradeRecordFromCall.setDateRecorded(now);
						}
						try {
							/** sychronize - add condition for null value */
							if (gradeRecordFromCall != null) {
								if (gradeRecordFromCall.getId() == null && isIquizCall && isUpdateAll && recordsFromCLDb != null) {
									AssignmentGradeRecord returnedPersistentItem = (AssignmentGradeRecord) recordsFromCLDb.get(gradeRecordFromCall.getStudentId());
									if (returnedPersistentItem != null && returnedPersistentItem.getPointsEarned() != null && gradeRecordFromCall.getPointsEarned() != null
											&& !returnedPersistentItem.getPointsEarned().equals(gradeRecordFromCall.getPointsEarned())) {
										graderId = gradeRecordFromCall.getGraderId();
										updated = true;
										returnedPersistentItem.setGraderId(gradeRecordFromCall.getGraderId());
										returnedPersistentItem.setPointsEarned(gradeRecordFromCall.getPointsEarned());
										returnedPersistentItem.setDateRecorded(gradeRecordFromCall.getDateRecorded());
										session.saveOrUpdate(returnedPersistentItem);
									} else if (returnedPersistentItem == null) {
										graderId = gradeRecordFromCall.getGraderId();
										updated = true;
										session.saveOrUpdate(gradeRecordFromCall);
									}
								} else {
									updated = true;
									session.saveOrUpdate(gradeRecordFromCall);
								}
							}
							if (!isUpdateAll && !isStudentView && synchronizer != null && !synchronizer.isProjectSite()) {
								Object updateIquizRecord = synchronizer.getNeededUpdateIquizRecord(assignment, gradeRecordFromCall);
								if (updateIquizRecord != null)
									legacyUpdates.add(updateIquizRecord);
							}
						} catch (TransientObjectException e) {
							// It's possible that a previously unscored student
							// was scored behind the current user's back before
							// the user saved the new score. This translates
							// that case into an optimistic locking failure.
							if (log.isInfoEnabled())
								log.info("An optimistic locking failure occurred while attempting to add a new assignment grade record");
							throw new StaleObjectModificationException(e);
						}

						// Check for excessive (AKA extra credit) scoring.
						/** synchronize - add condition for null value */
						if (gradeRecordFromCall != null && updated == true) {
							if (gradeRecordFromCall.getPointsEarned() != null && !assignment.isUngraded()
									&& gradeRecordFromCall.getPointsEarned().compareTo(assignment.getPointsPossible()) > 0) {
								studentsWithExcessiveScores.add(gradeRecordFromCall.getStudentId());
							}

							logAssignmentGradingEvent(gradeRecordFromCall, graderId, assignment, session);
							studentsWithUpdatedAssignmentGradeRecords.add(gradeRecordFromCall.getStudentId());
						}

						/** synchronize external records */
						if (legacyUpdates.size() > 0 && synchronizer != null) {
							synchronizer.updateLegacyGradeRecords(assignment.getName(), legacyUpdates);
						}
					}

				} else {
					for (Iterator iter = gradeRecordsFromCall.iterator(); iter.hasNext();) {
						AssignmentGradeRecord gradeRecordFromCall = (AssignmentGradeRecord) iter.next();
						gradeRecordFromCall.setGraderId(graderId);
						gradeRecordFromCall.setDateRecorded(now);
						try {
							session.saveOrUpdate(gradeRecordFromCall);
						} catch (TransientObjectException e) {
							// It's possible that a previously unscored student
							// was scored behind the current user's back before
							// the user saved the new score. This translates
							// that case into an optimistic locking failure.
							if (log.isInfoEnabled())
								log.info("An optimistic locking failure occurred while attempting to add a new assignment grade record");
							throw new StaleObjectModificationException(e);
						}

						// Check for excessive (AKA extra credit) scoring.
						if (gradeRecordFromCall.getPointsEarned() != null && !assignment.isUngraded()
								&& gradeRecordFromCall.getPointsEarned().compareTo(assignment.getPointsPossible()) > 0) {
							studentsWithExcessiveScores.add(gradeRecordFromCall.getStudentId());
						}

						// Log the grading event, and keep track of the students
						// with saved/updated grades
						logAssignmentGradingEvent(gradeRecordFromCall, graderId, assignment, session);

						studentsWithUpdatedAssignmentGradeRecords.add(gradeRecordFromCall.getStudentId());
					}
				}
				if (logData.isDebugEnabled())
					logData.debug("Updated " + studentsWithUpdatedAssignmentGradeRecords.size() + " assignment score records");

				return studentsWithExcessiveScores;
			}
		};

		Set studentsWithExcessiveScores = (Set) getHibernateTemplate().execute(hc);
		if (logData.isDebugEnabled())
			logData.debug("END: Update " + gradeRecordsFromCall.size() + " scores for gradebook=" + assignment.getGradebook().getUid() + ", assignment=" + assignment.getName());
		return studentsWithExcessiveScores;
	}

	public Set<AssignmentGradeRecord> updateAssignmentGradeRecords(Assignment assignment, Collection<AssignmentGradeRecord> gradeRecords, int grade_type) {
		if (grade_type == GradebookService.GRADE_TYPE_POINTS)
			return updateAssignmentGradeRecords(assignment, gradeRecords);
		else if (grade_type == GradebookService.GRADE_TYPE_PERCENTAGE) {
			Collection convertList = new ArrayList();
			for (Iterator iter = gradeRecords.iterator(); iter.hasNext();) {
				AssignmentGradeRecord agr = (AssignmentGradeRecord) iter.next();
				Double doubleValue = calculateDoublePointForRecord(agr);
				if (agr != null && doubleValue != null) {
					agr.setPointsEarned(doubleValue);
					convertList.add(agr);
				} else if (agr != null) {
					agr.setPointsEarned(null);
					convertList.add(agr);
				}
			}
			return updateAssignmentGradeRecords(assignment, convertList);
		} else if (grade_type == GradebookService.GRADE_TYPE_LETTER) {
			Collection convertList = new ArrayList();
			for (Iterator iter = gradeRecords.iterator(); iter.hasNext();) {
				AssignmentGradeRecord agr = (AssignmentGradeRecord) iter.next();
				Double doubleValue = calculateDoublePointForLetterGradeRecord(agr);
				if (agr != null && doubleValue != null) {
					agr.setPointsEarned(doubleValue);
					convertList.add(agr);
				} else if (agr != null) {
					agr.setPointsEarned(null);
					convertList.add(agr);
				}
			}
			return updateAssignmentGradeRecords(assignment, convertList);
		}

		else
			return null;
	}

	public void updateCategory(final Category category) throws ConflictingCategoryNameException, StaleObjectModificationException{
    	HibernateCallback hc = new HibernateCallback() {
    		public Object doInHibernate(Session session) throws HibernateException {
    			session.evict(category);
    			Category persistentCat = (Category)session.load(Category.class, category.getId());
    			List conflictList = ((List)session.createQuery(
    			"select ca from Category as ca where ca.name = ? and ca.gradebook = ? and ca.id != ? and ca.removed=false").
    			setString(0, category.getName()).
    			setEntity(1, category.getGradebook()).
    			setLong(2, category.getId().longValue()).list());
    			int numNameConflicts = conflictList.size();
    			if(numNameConflicts > 0) {
    				throw new ConflictingCategoryNameException("You can not save multiple category in a gradebook with the same name");
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

	public void updateComments(final Collection<Comment> comments) throws StaleObjectModificationException {
        final Date now = new Date();
        final String graderId = authn.getUserUid();

        // Unlike the complex grade update logic, this method assumes that
		// the client has done the work of filtering out any unchanged records
		// and isn't interested in throwing an optimistic locking exception for untouched records
		// which were changed by other sessions.
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				for (Iterator iter = comments.iterator(); iter.hasNext();) {
					Comment comment = (Comment)iter.next();
					comment.setGraderId(graderId);
					comment.setDateRecorded(now);
					session.saveOrUpdate(comment);
				}
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
			if(log.isInfoEnabled()) log.info("An optimistic locking failure occurred while attempting to update comments");
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
                        throw new IllegalStateException("Selected grade mapping can not be changed, since explicit course grades exist.");
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


	
	/*
	 * HELPER METHODS
	 * 
	 */
	
	// FIXME: Need to move this to GradeCalculations
	protected Double calculateDoublePointForRecord(AssignmentGradeRecord gradeRecordFromCall)
    {
    	Assignment assign = getAssignment(gradeRecordFromCall.getAssignment().getId()); 
    	Gradebook gradebook = getGradebook(assign.getGradebook().getId());
    	if(gradeRecordFromCall.getPercentEarned() != null)
    	{
    		if(gradeRecordFromCall.getPercentEarned().doubleValue() / 100.0 < 0)
    		{
    			throw new IllegalArgumentException("percent for record is less than 0 for percentage points in GradebookManagerHibernateImpl.calculateDoublePointForRecord");
    		}
    		return new Double(assign.getPointsPossible().doubleValue() * (gradeRecordFromCall.getPercentEarned().doubleValue() / 100.0));
    	}
    	else
    		return null;
    }
	
	// FIXME: Need to move this to GradeCalculations
	protected Double calculateDoublePointForLetterGradeRecord(AssignmentGradeRecord gradeRecordFromCall)
    {
    	Assignment assign = getAssignment(gradeRecordFromCall.getAssignment().getId()); 
    	Gradebook gradebook = getGradebook(assign.getGradebook().getId());
    	if(gradeRecordFromCall.getLetterEarned() != null)
    	{
    		LetterGradePercentMapping lgpm = getLetterGradePercentMapping(gradebook);
    		if(lgpm != null && lgpm.getGradeMap() != null)
    		{
    			Double doublePercentage = lgpm.getValue(gradeRecordFromCall.getLetterEarned());
    			if(doublePercentage == null)
    			{
    				log.error("percentage for " + gradeRecordFromCall.getLetterEarned() + " is not found in letter grade mapping in GradebookManagerHibernateImpl.calculateDoublePointForLetterGradeRecord");
    				return null;
    			}
    			
    			return calculateEquivalentPointValueForPercent(assign.getPointsPossible(), doublePercentage);
    		}
    		return null;
    	}
    	else
    		return null;
    }
	
	// FIXME: Need to move this to GradeCalculations
	protected Double calculateEquivalentPointValueForPercent(Double doublePointsPossible, Double doublePercentEarned) {
    	if (doublePointsPossible == null || doublePercentEarned == null)
    		return null;
    	
    	BigDecimal pointsPossible = new BigDecimal(doublePointsPossible.toString());
		BigDecimal percentEarned = new BigDecimal(doublePercentEarned.toString());
		BigDecimal equivPoints = pointsPossible.multiply(percentEarned.divide(new BigDecimal("100"), GradebookService.MATH_CONTEXT));
		return new Double(equivPoints.doubleValue());
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
	protected List<AbstractGradeRecord> filterGradeRecordsByStudents(Collection<AbstractGradeRecord> gradeRecords, Collection studentUids) {
		List<AbstractGradeRecord> filteredRecords = new ArrayList<AbstractGradeRecord>();
		for (AbstractGradeRecord agr : gradeRecords) {
			if (studentUids.contains(agr.getStudentId())) {
				filteredRecords.add(agr);
			}
		}
		return filteredRecords;
	}
	
	protected Set getAllStudentUids(String gradebookUid) {
		List enrollments = getSectionAwareness().getSiteMembersInRole(gradebookUid, Role.STUDENT);
        Set studentUids = new HashSet();
        for(Iterator iter = enrollments.iterator(); iter.hasNext();) {
            studentUids.add(((EnrollmentRecord)iter.next()).getUser().getUserUid());
        }
        return studentUids;
	}
	
	protected List<Assignment> getAssignments(Long gradebookId, Session session) throws HibernateException {
        List<Assignment> assignments = session.createQuery(
        	"from Assignment as asn where asn.gradebook.id=? and asn.removed=false").
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
	
	protected String getGradebookUid(Long id) {
        return ((Gradebook)getHibernateTemplate().load(Gradebook.class, id)).getUid();
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
        final Set studentUids = getAllStudentUids(getGradebookUid(gradebookId));
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
                    total = new Integer(totalList.size());
                    if (log.isInfoEnabled()) log.info("total number of explicitly entered course grade records = " + total);
                } else {
                    total = new Integer(0);
                    Query q = session.createQuery(
                            "select cgr.studentId from CourseGradeRecord as cgr where cgr.enteredGrade is not null and cgr.gradableObject.gradebook.id=:gradebookId");
                    q.setLong("gradebookId", gradebookId.longValue());
                    for (Iterator iter = q.list().iterator(); iter.hasNext(); ) {
                        String studentId = (String)iter.next();
                        if (studentUids.contains(studentId)) {
                            total = new Integer(1);
                            break;
                        }
                    }
                }
                return total;
            }
        };
        return ((Integer)getHibernateTemplate().execute(hc)).intValue() > 0;
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
		
		if (gradeRecord.isExcluded() != null && gradeRecord.isExcluded().booleanValue())
			gradeEntry = "excused";
		
		session.save(new GradingEvent(assignment, graderId, gradeRecord.getStudentId(), gradeEntry));
	}
	
	protected void updateAssignment(Assignment assignment, Session session) throws ConflictingAssignmentNameException, HibernateException {
		// Ensure that we don't have the assignment in the session, since
		// we need to compare the existing one in the db to our edited
		// assignment
		session.evict(assignment);

		Assignment asnFromDb = (Assignment) session.load(Assignment.class, assignment.getId());
		List conflictList = ((List) session.createQuery("select go from GradableObject as go where go.name = ? and go.gradebook = ? and go.removed=false and go.id != ?")
				.setString(0, assignment.getName()).setEntity(1, assignment.getGradebook()).setLong(2, assignment.getId().longValue()).list());
		int numNameConflicts = conflictList.size();
		if (numNameConflicts > 0) {
			throw new ConflictingAssignmentNameException("You can not save multiple assignments in a gradebook with the same name");
		}

		session.evict(asnFromDb);
		session.update(assignment);
	}
	
	protected void updateLetterGradePercentMapping(final Map gradeMap, final Gradebook gradebook) {
		HibernateCallback hcb = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				LetterGradePercentMapping lgpm = getLetterGradePercentMapping(gradebook);

				if (lgpm == null)
					throw new IllegalArgumentException("LetterGradePercentMapping is null in BaseHibernateManager.updateLetterGradePercentMapping");

				if (gradeMap == null)
					throw new IllegalArgumentException("gradeMap is null in BaseHibernateManager.updateLetterGradePercentMapping");

				Set keySet = gradeMap.keySet();

				if (keySet.size() != GradebookService.validLetterGrade.length) // we only consider letter grade with -/+ now.
					throw new IllegalArgumentException("gradeMap doesn't have right size in BaseHibernateManager.udpateLetterGradePercentMapping");

				if (validateLetterGradeMapping(gradeMap) == false)
					throw new IllegalArgumentException("gradeMap contains invalid letter in BaseHibernateManager.udpateLetterGradePercentMapping");

				Map saveMap = new HashMap();
				for (Iterator iter = gradeMap.keySet().iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					saveMap.put(key, gradeMap.get(key));
				}

				lgpm.setGradeMap(saveMap);
				session.save(lgpm);

				return null;
			}
		};
		getHibernateTemplate().execute(hcb);
	}
	
    protected boolean validateLetterGradeMapping(Map gradeMap) {
    	Set keySet = gradeMap.keySet();

    	for(Iterator iter = keySet.iterator(); iter.hasNext(); )
    	{
    		String key = (String) iter.next();
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
	
    
	/*
	 * DEPENDENCY INJECTION ACCESSORS
	 * 
	 */
	
	public SectionAwareness getSectionAwareness() {
		return sectionAwareness;
	}

	public void setSectionAwareness(SectionAwareness sectionAwareness) {
		this.sectionAwareness = sectionAwareness;
	}

	public Authn getAuthn() {
		return authn;
	}

	public void setAuthn(Authn authn) {
		this.authn = authn;
	}

	public EventTrackingService getEventTrackingService() {
		return eventTrackingService;
	}

	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}
	
	/** synchronize from external application */
    public void setSynchronizer(GbSynchronizer synchronizer) 
    {
    	this.synchronizer = synchronizer;
    }

}
