package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.w3c.dom.Document;

public class ToolManagerMock implements ToolManager {

	public Set<Tool> findTools(Set<String> arg0, Set<String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Placement getCurrentPlacement() {
		
		return new PlacementMock("toolid00-7c76-43ed-8090-1d6cbbf15a1b", null, null, null, AppConstants.TEST_SITE_CONTEXT_ID, null);
	}

	public Tool getCurrentTool() {
		// TODO Auto-generated method stub
		return null;
	}

	public Tool getTool(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void register(Tool arg0) {
		// TODO Auto-generated method stub

	}

	public void register(Document arg0) {
		// TODO Auto-generated method stub

	}

	public void register(File arg0) {
		// TODO Auto-generated method stub

	}

	public void register(InputStream arg0) {
		// TODO Auto-generated method stub

	}

	public void setResourceBundle(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	public boolean isVisible(Site arg0, ToolConfiguration arg1) {
		return true;
	}

	@Override
	public String getLocalizedToolProperty(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
