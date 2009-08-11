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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Authn;
import org.sakaiproject.gradebook.gwt.sakai.model.Realm;
import org.sakaiproject.gradebook.gwt.sakai.model.RealmGroup;
import org.sakaiproject.gradebook.gwt.sakai.model.RealmRole;
import org.sakaiproject.service.gradebook.shared.GradebookExistsException;
import org.sakaiproject.service.gradebook.shared.GradebookFrameworkService;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.tool.gradebook.CourseGrade;
import org.sakaiproject.tool.gradebook.GradeMapping;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.tool.gradebook.GradebookProperty;
import org.sakaiproject.tool.gradebook.GradingScale;
import org.sakaiproject.tool.gradebook.LetterGradeMapping;
import org.sakaiproject.tool.gradebook.LetterGradePercentMapping;
import org.sakaiproject.tool.gradebook.LetterGradePlusMinusMapping;
import org.sakaiproject.tool.gradebook.PassNotPassMapping;
import org.sakaiproject.tool.gradebook.facades.Authn;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GradebookFrameworkServiceMock extends HibernateDaoSupport implements GradebookFrameworkService {

	private static final Log log = LogFactory.getLog(GradebookFrameworkServiceMock.class);

	public static final String UID_OF_DEFAULT_GRADING_SCALE_PROPERTY = "uidOfDefaultGradingScale";
	
	private Gradebook2Authn authn;
	
	public void addGradebook(final String uid, final String name) {
		if(isGradebookDefined(uid)) {
            log.warn("You can not add a gradebook with uid=" + uid + ".  That gradebook already exists.");
            throw new GradebookExistsException("You can not add a gradebook with uid=" + uid + ".  That gradebook already exists.");
        }
        if (log.isInfoEnabled()) log.info("Adding gradebook uid=" + uid + " by userUid=" + getUserUid());

        createDefaultLetterGradeMapping(getHardDefaultLetterMapping());
        
        getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				// Get available grade mapping templates.
				List gradingScales = session.createQuery("from GradingScale as gradingScale where gradingScale.unavailable=false").list();

				// The application won't be able to run without grade mapping
				// templates, so if for some reason none have been defined yet,
				// do that now.
				if (gradingScales.isEmpty()) {
					if (log.isInfoEnabled()) log.info("No Grading Scale defined yet. This is probably because you have upgraded or you are working with a new database. Default grading scales will be created. Any customized system-wide grade mappings you may have defined in previous versions will have to be reconfigured.");
					gradingScales = addDefaultGradingScales(session);
				}

				// Create and save the gradebook
				Gradebook gradebook = new Gradebook(name);
				gradebook.setUid(uid);
				session.save(gradebook);
				
				List list = getHibernateTemplate().find(
						"from Gradebook as gb where gb.uid=?", uid);

				// Create the course grade for the gradebook
				CourseGrade cg = new CourseGrade();
				cg.setGradebook(gradebook);
				session.save(cg);

				// According to the specification, Display Assignment Grades is
				// on by default, and Display course grade is off.
				gradebook.setAssignmentsDisplayed(true);
				gradebook.setCourseGradeDisplayed(false);

				String defaultScaleUid = getPropertyValue(UID_OF_DEFAULT_GRADING_SCALE_PROPERTY);

				// Add and save grade mappings based on the templates.
				GradeMapping defaultGradeMapping = null;
				Set gradeMappings = new HashSet();
				for (Iterator iter = gradingScales.iterator(); iter.hasNext();) {
					GradingScale gradingScale = (GradingScale)iter.next();
					GradeMapping gradeMapping = new GradeMapping(gradingScale);
					gradeMapping.setGradebook(gradebook);
					session.save(gradeMapping);
					gradeMappings.add(gradeMapping);
					if (gradingScale.getUid().equals(defaultScaleUid)) {
						defaultGradeMapping = gradeMapping;
					}
				}

				// Check for null default.
				if (defaultGradeMapping == null) {
					defaultGradeMapping = (GradeMapping)gradeMappings.iterator().next();
					if (log.isWarnEnabled()) log.warn("No default GradeMapping found for new Gradebook=" + gradebook.getUid() + "; will set default to " + defaultGradeMapping.getName());
				}
				gradebook.setSelectedGradeMapping(defaultGradeMapping);

				// The Hibernate mapping as of Sakai 2.2 makes this next
				// call meaningless when it comes to persisting changes at
				// the end of the transaction. It is, however, needed for
				// the mappings to be seen while the transaction remains
				// uncommitted.
				gradebook.setGradeMappings(gradeMappings);
				
				gradebook.setGrade_type(GradebookService.GRADE_TYPE_POINTS);
				gradebook.setCategory_type(GradebookService.CATEGORY_TYPE_NO_CATEGORY);

				// Update the gradebook with the new selected grade mapping
				session.update(gradebook);

				
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

	public void deleteGradebook(String arg0) throws GradebookNotFoundException {
		// TODO Auto-generated method stub

	}

	public boolean isGradebookDefined(String gradebookUid) {
        String hql = "from Gradebook as gb where gb.uid=?";
        return getHibernateTemplate().find(hql, gradebookUid).size() == 1;
    }

	public void setAvailableGradingScales(Collection arg0) {
		// TODO Auto-generated method stub

	}

	public void setDefaultGradingScale(String arg0) {
		// TODO Auto-generated method stub

	}
	
	public Gradebook2Authn getAuthn() {
        return authn;
    }
    public void setAuthn(Gradebook2Authn authn) {
        this.authn = authn;
    }

    protected String getUserUid() {
        return authn.getUserUid();
    }
	
    private List addDefaultGradingScales(Session session) throws HibernateException {
    	List gradingScales = new ArrayList();

    	// Base the default set of templates on the old
    	// statically defined GradeMapping classes.
    	GradeMapping[] oldGradeMappings = {
    		new LetterGradeMapping(),
    		new LetterGradePlusMinusMapping(),
    		new PassNotPassMapping()
    	};

    	for (int i = 0; i < oldGradeMappings.length; i++) {
    		GradeMapping sampleMapping = oldGradeMappings[i];
    		sampleMapping.setDefaultValues();
			GradingScale gradingScale = new GradingScale();
			String uid = sampleMapping.getClass().getName();
			uid = uid.substring(uid.lastIndexOf('.') + 1);
			gradingScale.setUid(uid);
			gradingScale.setUnavailable(false);
			gradingScale.setName(sampleMapping.getName());
			gradingScale.setGrades(new ArrayList(sampleMapping.getGrades()));
			gradingScale.setDefaultBottomPercents(new HashMap(sampleMapping.getGradeMap()));
			session.save(gradingScale);
			if (log.isInfoEnabled()) log.info("Added Grade Mapping " + gradingScale.getUid());
			gradingScales.add(gradingScale);
		}
		setDefaultGradingScale("LetterGradePlusMinusMapping");
		session.flush();
		return gradingScales;
	}
    
	private LetterGradePercentMapping getDefaultLetterGradePercentMapping()
    {
    	HibernateCallback hc = new HibernateCallback() 
    	{
    		public Object doInHibernate(Session session) throws HibernateException 
    		{
    			List defaultMapping = (session.createQuery(
    			"select lgpm from LetterGradePercentMapping as lgpm where lgpm.mappingType = 1")).list();
    			if(defaultMapping == null || defaultMapping.size() == 0) 
    			{
    				log.info("Default letter grade mapping hasn't been created in DB in BaseHibernateManager.getDefaultLetterGradePercentMapping");
    				return null;
    			}
    			if(defaultMapping.size() > 1) 
    			{
    				log.error("Duplicate default letter grade mapping was created in DB in BaseHibernateManager.getDefaultLetterGradePercentMapping");
    				return null;
    			}

    			return ((LetterGradePercentMapping) defaultMapping.get(0));

    		}
    	};

    	return (LetterGradePercentMapping) getHibernateTemplate().execute(hc);
    }
	
	private void createDefaultLetterGradeMapping(final Map gradeMap)
	{
		if(getDefaultLetterGradePercentMapping() == null)
		{	
			Set keySet = gradeMap.keySet();

			if(keySet.size() != GradebookService.validLetterGrade.length) //we only consider letter grade with -/+ now.
				throw new IllegalArgumentException("gradeMap doesn't have right size in BaseHibernateManager.createDefaultLetterGradePercentMapping");

			if(validateLetterGradeMapping(gradeMap) == false)
				throw new IllegalArgumentException("gradeMap contains invalid letter in BaseHibernateManager.createDefaultLetterGradePercentMapping");

			HibernateCallback hc = new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					LetterGradePercentMapping lgpm = new LetterGradePercentMapping();
					session.save(lgpm);
					Map saveMap = new HashMap();
					for(Iterator iter = gradeMap.keySet().iterator(); iter.hasNext();)
					{
						String key = (String) iter.next();
						saveMap.put(key, gradeMap.get(key));
					}
					if (lgpm != null)
					{                    
						lgpm.setGradeMap(saveMap);
						lgpm.setMappingType(1);
						session.update(lgpm);
					}
					return null;
				}
			};
			getHibernateTemplate().execute(hc);
		}
	}
	
	private Map getHardDefaultLetterMapping()
	  {
	  	Map gradeMap = new HashMap();
			gradeMap.put("A+", new Double(100));
			gradeMap.put("A", new Double(95));
			gradeMap.put("A-", new Double(90));
			gradeMap.put("B+", new Double(87));
			gradeMap.put("B", new Double(83));
			gradeMap.put("B-", new Double(80));
			gradeMap.put("C+", new Double(77));
			gradeMap.put("C", new Double(73));
			gradeMap.put("C-", new Double(70));
			gradeMap.put("D+", new Double(67));
			gradeMap.put("D", new Double(63));
			gradeMap.put("D-", new Double(60));
			gradeMap.put("F", new Double(0.0));
			
			return gradeMap;
	  }

	protected Map propertiesMap = new HashMap();
	
	private String getPropertyValue(final String name) {
		String value = (String)propertiesMap.get(name);
		if (value == null) {
			List list = getHibernateTemplate().find("from GradebookProperty as prop where prop.name=?",
				name);
			if (!list.isEmpty()) {
				GradebookProperty property = (GradebookProperty)list.get(0);
				value = property.getValue();
				propertiesMap.put(name, value);
			}
		}
		return value;
	}
	
	private boolean validateLetterGradeMapping(Map gradeMap)
    {
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
}
