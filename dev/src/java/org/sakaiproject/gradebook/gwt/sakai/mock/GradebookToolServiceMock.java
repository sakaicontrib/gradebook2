package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.sakaiproject.gradebook.gwt.sakai.hibernate.GradebookToolServiceImpl;
import org.springframework.orm.hibernate3.HibernateCallback;

public class GradebookToolServiceMock extends GradebookToolServiceImpl {

	@Override
	public List<String> getFullUserListForSite(final String siteId,
			final String[] roleNames) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {

				List<String> uids = new ArrayList<String>();
				for (int i = 0; i < UserDirectoryServiceMock.DEFAULT_NUMBER_TEST_LEARNERS; i++) {
					uids.add("" + i);
				}
				return uids;
			}
		};

		List<String> result = (List<String>) getHibernateTemplate().execute(hc);

		return result;
	}

	@Override
	public List<Object[]> getUserGroupReferences(
			final List<String> groupReferences, final String[] roleNames) {

		if (groupReferences == null || groupReferences.size() == 0)
			return new ArrayList<Object[]>();

		final Integer perGroup = (int) Math
				.floor(UserDirectoryServiceMock.DEFAULT_NUMBER_TEST_LEARNERS
						/ groupReferences.size());
		final Integer remainder = UserDirectoryServiceMock.DEFAULT_NUMBER_TEST_LEARNERS
				% groupReferences.size();

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {

				ArrayList<Object[]> rv = new ArrayList<Object[]>();

				for (int i = 0; i < groupReferences.size(); ++i) {
					for (int j = 0; j < perGroup; ++j) {
						rv.add(new Object[] { "" + (j + i), groupReferences.get(i) });
					}
				}
				for (int i = 0; i < remainder; ++i) {
					rv.add(new Object[] {
							groupReferences.size() + perGroup + i,
							groupReferences.get(groupReferences.size() - 1) });
				}

				return rv;
			}
		};
		return (List<Object[]>) getHibernateTemplate().execute(hc);
	}

	public int getUserCountForSite(final String[] realmIds,
			final String sortField, final String searchField,
			final String searchCriteria, final String[] roleNames) {

		return UserDirectoryServiceMock.DEFAULT_NUMBER_TEST_LEARNERS;

	}

}
