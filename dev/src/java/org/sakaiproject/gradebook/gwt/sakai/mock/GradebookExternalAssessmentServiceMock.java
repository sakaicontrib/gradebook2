package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.sakaiproject.service.gradebook.shared.AssessmentNotFoundException;
import org.sakaiproject.service.gradebook.shared.AssignmentHasIllegalPointsException;
import org.sakaiproject.service.gradebook.shared.ConflictingAssignmentNameException;
import org.sakaiproject.service.gradebook.shared.ConflictingExternalIdException;
import org.sakaiproject.service.gradebook.shared.ExternalAssignmentProvider;
import org.sakaiproject.service.gradebook.shared.GradebookExternalAssessmentService;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.service.gradebook.shared.InvalidCategoryException;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GradebookExternalAssessmentServiceMock extends HibernateDaoSupport 
	implements GradebookExternalAssessmentService {

	private static final Log log = LogFactory.getLog(GradebookExternalAssessmentServiceMock.class);
	
	public void addExternalAssessment(final String gradebookUid, final String externalId, final String externalUrl,
			final String title, final double points, final Date dueDate, final String externalServiceDescription)
            throws ConflictingAssignmentNameException, ConflictingExternalIdException, GradebookNotFoundException {

        // Ensure that the required strings are not empty
        if(StringUtils.trimToNull(externalServiceDescription) == null ||
                StringUtils.trimToNull(externalId) == null ||
                StringUtils.trimToNull(title) == null) {
            throw new RuntimeException("External service description, externalId, and title must not be empty");
        }

        // Ensure that points is > zero
        if(points <= 0) {
            throw new AssignmentHasIllegalPointsException("Points must be > 0");
        }

        // Ensure that the assessment name is unique within this gradebook
		if (isAssignmentDefined(gradebookUid, title)) {
            throw new ConflictingAssignmentNameException("An assignment with that name already exists in gradebook uid=" + gradebookUid);
        }

		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				// Ensure that the externalId is unique within this gradebook
				List conflictList = (List)session.createQuery(
					"select asn from Assignment as asn where asn.externalId=? and asn.gradebook.uid=?").
					setString(0, externalId).
					setString(1, gradebookUid).list();
				Integer externalIdConflicts = conflictList.size();
				if (externalIdConflicts.intValue() > 0) {
					throw new ConflictingExternalIdException("An external assessment with that ID already exists in gradebook uid=" + gradebookUid);
				}

				// Get the gradebook
				Gradebook gradebook = getGradebook(gradebookUid);

				// Create the external assignment
				Assignment asn = new Assignment(gradebook, title, Double.valueOf(points), dueDate);
				asn.setExternallyMaintained(true);
				asn.setExternalId(externalId);
				asn.setExternalInstructorLink(externalUrl);
				asn.setExternalStudentLink(externalUrl);
				asn.setExternalAppName(externalServiceDescription);
                //set released to be true to support selective release
                asn.setReleased(true);
                asn.setUngraded(false);

                session.save(asn);
				return null;
			}
		});
        if (log.isInfoEnabled()) log.info("External assessment added to gradebookUid=" + gradebookUid + ", externalId=" + externalId + " from externalApp=" + externalServiceDescription);
	}
	
	public Gradebook getGradebook(String uid) throws GradebookNotFoundException {
    	List list = getHibernateTemplate().find("from Gradebook as gb where gb.uid=?",
    		uid);
		if (list.size() == 1) {
			return (Gradebook)list.get(0);
		} else {
            throw new GradebookNotFoundException("Could not find gradebook uid=" + uid);
        }
    }

	public void addExternalAssessment(final String gradebookUid, final String externalId, final String externalUrl, final String title, final Double points, 
			final Date dueDate, final String externalServiceDescription, final Boolean ungraded) 
			throws GradebookNotFoundException, ConflictingAssignmentNameException, ConflictingExternalIdException, AssignmentHasIllegalPointsException
	{
		addExternalAssessment(gradebookUid, externalId, externalUrl, title, points, dueDate, externalServiceDescription, ungraded, null);
	}

	public void addExternalAssessment(final String gradebookUid, final String externalId, final String externalUrl, final String title, final Double points, 
			final Date dueDate, final String externalServiceDescription, final Boolean ungraded, final Long categoryId) 
			throws GradebookNotFoundException, ConflictingAssignmentNameException, ConflictingExternalIdException, AssignmentHasIllegalPointsException
		{
			// Ensure that the required strings are not empty
			if(StringUtils.trimToNull(externalServiceDescription) == null ||
					StringUtils.trimToNull(externalId) == null ||
					StringUtils.trimToNull(title) == null) {
				throw new RuntimeException("External service description, externalId, and title must not be empty");
			}

			// Ensure that points is > zero
			if((ungraded != null && !ungraded.booleanValue() && (points == null ||  points.doubleValue() <= 0))
					|| (ungraded == null && (points == null ||  points.doubleValue() <= 0))) {
				throw new AssignmentHasIllegalPointsException("Points can't be null or Points must be > 0");
			}

			// Ensure that the assessment name is unique within this gradebook
			if (isAssignmentDefined(gradebookUid, title)) {
				throw new ConflictingAssignmentNameException("An assignment with that name already exists in gradebook uid=" + gradebookUid);
			}

			getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					// Ensure that the externalId is unique within this gradebook
					List conflictList = (List)session.createQuery(
					"select asn from Assignment as asn where asn.externalId=? and asn.gradebook.uid=?").
					setString(0, externalId).
					setString(1, gradebookUid).list();
					Integer externalIdConflicts = conflictList.size();
					if (externalIdConflicts.intValue() > 0) {
						throw new ConflictingExternalIdException("An external assessment with that ID already exists in gradebook uid=" + gradebookUid);
					}

					// Get the gradebook
					Gradebook gradebook = getGradebook(gradebookUid);
					
					// if a category was indicated, double check that it is valid
					Category persistedCategory = null;
					if (categoryId != null) {
					    persistedCategory = getCategory(categoryId);
					    if (persistedCategory == null || persistedCategory.isRemoved() ||
					            !persistedCategory.getGradebook().getId().equals(gradebook.getId())) {
					        throw new InvalidCategoryException("The category with id " + categoryId + 
					                " is not valid for gradebook " + gradebook.getUid());
					    }
					}

					// Create the external assignment
					Assignment asn = new Assignment(gradebook, title, points, dueDate);
					asn.setExternallyMaintained(true);
					asn.setExternalId(externalId);
					asn.setExternalInstructorLink(externalUrl);
					asn.setExternalStudentLink(externalUrl);
					asn.setExternalAppName(externalServiceDescription);
					if (persistedCategory != null) { 
						asn.setCategory(persistedCategory);
					}
					//set released to be true to support selective release
					asn.setReleased(true);
					if(ungraded != null)
						asn.setUngraded(ungraded.booleanValue());
					else
						asn.setUngraded(false);

					session.save(asn);
					return null;
				}
			});
			if (log.isInfoEnabled()) log.info("External assessment added to gradebookUid=" + gradebookUid + ", externalId=" + externalId + " from externalApp=" + externalServiceDescription);
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
	
	public boolean isAssignmentDefined(final String gradebookUid, final String assignmentName)
    throws GradebookNotFoundException {
	    Assignment assignment = (Assignment)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return getAssignmentWithoutStats(gradebookUid, assignmentName, session);
			}
		});
	    return (assignment != null);
	}
	
	protected Assignment getAssignmentWithoutStats(String gradebookUid, String assignmentName, Session session) throws HibernateException {
		return (Assignment)session.createQuery(
			"from Assignment as asn where asn.name=? and asn.gradebook.uid=? and asn.removed=false").
			setString(0, assignmentName).
			setString(1, gradebookUid).
			uniqueResult();
	}

	public boolean isExternalAssignmentDefined(String gradebookUid, String externalId) throws GradebookNotFoundException {
        Assignment assignment = getExternalAssignment(gradebookUid, externalId);
        return (assignment != null);
	}
	
	private Assignment getExternalAssignment(final String gradebookUid, final String externalId) throws GradebookNotFoundException {
        final Gradebook gradebook = getGradebook(gradebookUid);

        HibernateCallback hc = new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
				return session.createQuery(
					"from Assignment as asn where asn.gradebook=? and asn.externalId=?").
					setEntity(0, gradebook).
					setString(1, externalId).
					uniqueResult();
            }
        };
        return (Assignment)getHibernateTemplate().execute(hc);
    }

	public boolean isGradebookDefined(String gradebookUid) {
        String hql = "from Gradebook as gb where gb.uid=?";
        return getHibernateTemplate().find(hql, gradebookUid).size() == 1;
    }

	public void removeExternalAssessment(String arg0, String arg1)
			throws GradebookNotFoundException, AssessmentNotFoundException {
		// TODO Auto-generated method stub

	}

	public void setExternalAssessmentToGradebookAssignment(String arg0,
			String arg1) {
		// TODO Auto-generated method stub

	}

	public void updateExternalAssessment(String arg0, String arg1, String arg2,
			String arg3, double arg4, Date arg5)
			throws GradebookNotFoundException, AssessmentNotFoundException,
			ConflictingAssignmentNameException,
			AssignmentHasIllegalPointsException {
		// TODO Auto-generated method stub

	}

	public void updateExternalAssessment(String arg0, String arg1, String arg2,
			String arg3, Double arg4, Date arg5, Boolean arg6)
			throws GradebookNotFoundException, AssessmentNotFoundException,
			ConflictingAssignmentNameException,
			AssignmentHasIllegalPointsException {
		// TODO Auto-generated method stub

	}

	public void updateExternalAssessmentScore(String arg0, String arg1,
			String arg2, String arg3) throws GradebookNotFoundException,
			AssessmentNotFoundException {
		// TODO Auto-generated method stub

	}

//	public void updateExternalAssessmentScores(String arg0, String arg1,
//			Map arg2) throws GradebookNotFoundException,
//			AssessmentNotFoundException {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void updateExternalAssessmentScoresString(String arg0, String arg1,
//			Map arg2) throws GradebookNotFoundException,
//			AssessmentNotFoundException {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public boolean isExternalAssignmentGrouped(String arg0, String arg1)
			throws GradebookNotFoundException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExternalAssignmentVisible(String arg0, String arg1,
			String arg2) throws GradebookNotFoundException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerExternalAssignmentProvider(
			ExternalAssignmentProvider arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterExternalAssignmentProvider(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getExternalAssignmentsForCurrentUser(String arg0)
			throws GradebookNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateExternalAssessmentScores(String arg0, String arg1,
			Map arg2) throws GradebookNotFoundException,
			AssessmentNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateExternalAssessmentScoresString(String arg0, String arg1,
			Map arg2) throws GradebookNotFoundException,
			AssessmentNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, List<String>> getVisibleExternalAssignments(String arg0,
			Collection<String> arg1) throws GradebookNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
