package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.List;
import java.util.Locale;
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

		String configProperty = System.getProperty(arg0);
		
		if(configProperty == null || "".equals(configProperty)) {
			return arg1;
		}
		else if(configProperty.equalsIgnoreCase("true")) {
			return true;
		}
		else if(configProperty.equalsIgnoreCase("false")) {
			return false;
		}
		else {
			// In case it's misspelled 
			return arg1;
		}
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

		String configProperty = System.getProperty(arg0);
		
		if(configProperty == null || "".equals(configProperty)) {
			
			return arg1;
		}
		
		int num;
		
		try {
			
			num = Integer.parseInt(configProperty);
		}
		catch(NumberFormatException nfe) {
			
			num = arg1;
		}
		
		return num;
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
		
		String configProperty = System.getProperty(arg0);
		
		if(null == configProperty || "".equals(configProperty)) {
			return arg1;
		}
		return configProperty;
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

	@Override
	public <T> T getConfig(String arg0, T arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigData getConfigData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigItem getConfigItem(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigItem registerConfigItem(ConfigItem arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerListener(ConfigurationListener arg0) {
		// TODO Auto-generated method stub
		
	}

	public List<String> getCategoryGroups(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Locale getLocaleFromString(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Locale[] getSakaiLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<String> getToolGroup(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean toolGroupIsRequired(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean toolGroupIsSelected(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
