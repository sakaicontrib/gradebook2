package org.sakaiproject.gradebook.gwt.client.gxt;

import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultigradeSelectionModel;

public class GbGridCallback {

	private MultigradeSelectionModel sm;

    public GbGridCallback(MultigradeSelectionModel sm) {
      this.sm = sm;
    }

    public boolean isSelectable(int row, int cell, boolean acceptsNav) {
      return sm.isCellSelectable(row, cell, acceptsNav);
    }
	
}
