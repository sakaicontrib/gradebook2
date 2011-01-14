package org.sakaiproject.gradebook.gwt.sakai;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

public class CalculatedGradeComparator implements Comparator<UserRecord>, Serializable {

	private static final long serialVersionUID = 1L;
	protected boolean isDesc;

	public CalculatedGradeComparator(boolean isDesc) {
		this.isDesc = isDesc;
	}
	
	public int compare(UserRecord o1, UserRecord o2) {
		BigDecimal b1 = getCalculatedGrade(o1);
		BigDecimal b2 = getCalculatedGrade(o2);
		
		if (b2 == null) 
			return isDesc ? 1 : -1;
		if (b1 == null)
			return isDesc ? -1 : 1;

		return b2.compareTo(b1);
	}

	protected BigDecimal getCalculatedGrade(UserRecord record) {
		if (null == record || null == record.getDisplayGrade())
			return null;

		return record.getDisplayGrade().getCalculatedGrade();
	}
}
