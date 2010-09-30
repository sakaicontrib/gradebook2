package org.sakaiproject.gradebook.gwt.sakai.model;

import java.math.BigDecimal;

public class StudentScore implements Comparable<StudentScore>
{

	private String userUid; 
	private BigDecimal score;

	public StudentScore() 
	{
		userUid = null; 
		score = null; 
	}
	
	public StudentScore(String userUid, BigDecimal score) {
		super();
		this.userUid = userUid;
		this.score = score;
	}

	public String getUserUid() {
		return userUid;
	}
	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}
	public BigDecimal getScore() {
		return score;
	}
	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public int compareTo(StudentScore o) 
	{
		if (o != null && o.getScore() != null && getScore() != null) 
		{
			return getScore().compareTo(o.getScore()); 
		}
		return -1; 
	}
	
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   StudentScore rhs = (StudentScore) obj;
		   return getScore().equals(rhs.getScore());
	}
	
	public String toString() {
		return "StudentScore@" + hashCode() + " (id,score) = (" + userUid + "," + score + ")";
	}
	
	
}
