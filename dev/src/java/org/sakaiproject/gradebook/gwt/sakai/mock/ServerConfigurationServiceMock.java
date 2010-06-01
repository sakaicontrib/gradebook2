package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.List;
import java.util.Map;

import org.sakaiproject.component.api.ServerConfigurationService;

public class ServerConfigurationServiceMock implements ServerConfigurationService {

	public String getAccessPath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAccessUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getBoolean(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public List<String> getDefaultTools(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getGatewaySiteId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHelpUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getInt(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getLoggedOutUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPortalUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRawProperty(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSakaiHomePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServerId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServerIdInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServerInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServerUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String property) {
		// GRBK-617 : Remove getProperty prefix "gb2.config."
		String configProperty = System.getProperty(property);
		return configProperty;
	}

	public String getString(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getStrings(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getToolCategories(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<String>> getToolCategoriesAsMap(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getToolOrder(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getToolToCategoryMap(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getToolsRequired(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserHomeUrl() {
		// TODO Auto-generated method stub
		return null;
	}

}
