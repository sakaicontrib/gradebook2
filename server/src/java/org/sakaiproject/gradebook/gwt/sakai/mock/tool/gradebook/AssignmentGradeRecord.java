/**********************************************************************************
*
* $Id: AssignmentGradeRecord.java 39738 2008-01-03 20:24:05Z cwen@iupui.edu $
*
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of California, The MIT Corporation
*
* Licensed under the Educational Community License, Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.opensource.org/licenses/ecl1.php
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/

package org.sakaiproject.gradebook.gwt.sakai.mock.tool.gradebook;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Comparator;


/**
 * An AssignmentGradeRecord is a grade record that can be associated with an
 * Assignment.
 *
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class AssignmentGradeRecord extends AbstractGradeRecord {
	public static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_DOWN);
	
	private Double pointsEarned;
    private String letterEarned;
    private Double percentEarned;
    private boolean userAbleToView;
    private Boolean excluded;
    private transient BigDecimal earnedWeightedPercentage;

    public AssignmentGradeRecord() {
        super();
    }

    /**
     * The graderId and dateRecorded properties will be set explicitly by the
     * grade manager before the database is updated.
	 * @param assignment The assignment this grade record is associated with
     * @param studentId The student id for whom this grade record belongs
	 * @param grade The grade, or points earned
	 */
	public AssignmentGradeRecord(Assignment assignment, String studentId, Double grade) {
        super();
        this.gradableObject = assignment;
        this.studentId = studentId;
        this.pointsEarned = grade;
	}
	
	public static Comparator<AssignmentGradeRecord> calcComparator;

    static {
        calcComparator = new Comparator<AssignmentGradeRecord>() {
            public int compare(AssignmentGradeRecord agr1, AssignmentGradeRecord agr2) {
                if(agr1 == null && agr2 == null) {
                    return 0;
                }
                if(agr1 == null) {
                    return -1;
                }
                if(agr2 == null) {
                    return 1;
                }
                Double agr1Points = agr1.getPointsEarned();
                Double agr2Points = agr2.getPointsEarned();
                
                if (agr1Points == null && agr2Points == null) {
                	return 0;
                }
                if (agr1Points == null && agr2Points != null) {
                	return -1;
                }
                if (agr1Points != null && agr2Points == null) {
                	return 1;
                }
                return agr1Points.compareTo(agr2Points);
            }
        };
    }

    /**
     * @return Returns the pointsEarned
     */
    public Double getPointsEarned() {
        return pointsEarned;
    }

	/**
	 * @param pointsEarned The pointsEarned to set.
	 */
	public void setPointsEarned(Double pointsEarned) {
		this.pointsEarned = pointsEarned;
	}

    /**
     * Returns null if the points earned is null.  Otherwise, returns earned / points possible * 100.
     *
     * @see org.sakaiproject.gradebook.gwt.sakai.mock.tool.gradebook.AbstractGradeRecord#getGradeAsPercentage()
     */
    public Double getGradeAsPercentage() {
        if (pointsEarned == null) {
            return null;
        }
        BigDecimal bdPointsEarned = new BigDecimal(pointsEarned.toString());
        BigDecimal bdPossible = new BigDecimal(((Assignment)getGradableObject()).getPointsPossible().toString());
        BigDecimal bdPercent = bdPointsEarned.divide(bdPossible, MATH_CONTEXT).multiply(new BigDecimal("100"));
        return new Double(bdPercent.doubleValue());
    }

	/**
	 * @see org.sakaiproject.gradebook.gwt.sakai.mock.tool.gradebook.AbstractGradeRecord#isCourseGradeRecord()
	 */
	public boolean isCourseGradeRecord() {
		return false;
	}

    public Assignment getAssignment() {
    	return (Assignment)getGradableObject();
    }
    
    public Double getPercentEarned() {
    	return percentEarned;
    }
    
    public void setPercentEarned(Double percentEarned) {
    	this.percentEarned = percentEarned;
    }

    public String getLetterEarned()
    {
    	return letterEarned;
    }

    public void setLetterEarned(String letterEarned)
    {
    	this.letterEarned = letterEarned;
    }
    
    public boolean isUserAbleToView() {
    	return userAbleToView;
    }
    public void setUserAbleToView(boolean userAbleToView) {
    	this.userAbleToView = userAbleToView;
    }

    public AssignmentGradeRecord clone()
    {
    	AssignmentGradeRecord agr = new AssignmentGradeRecord();
    	agr.setDateRecorded(dateRecorded);
    	agr.setGradableObject(gradableObject);
    	agr.setGraderId(graderId);
    	agr.setLetterEarned(letterEarned);
    	agr.setPointsEarned(pointsEarned);
    	agr.setPercentEarned(percentEarned);
    	agr.setStudentId(studentId);
    	return agr;
    }

	public Boolean isExcluded() {
		return excluded;
	}

	public void setExcluded(Boolean excluded) {
		this.excluded = excluded;
	}

	public BigDecimal getEarnedWeightedPercentage() {
		return earnedWeightedPercentage;
	}

	public void setEarnedWeightedPercentage(BigDecimal earnedWeightedPercentage) {
		this.earnedWeightedPercentage = earnedWeightedPercentage;
	}
}


