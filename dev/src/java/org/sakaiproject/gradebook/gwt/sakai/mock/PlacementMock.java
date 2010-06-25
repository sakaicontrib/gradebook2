package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.Properties;

import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Tool;

public class PlacementMock implements Placement {
	
	private String m_id;
	//private String m_toolId;
	//private Tool m_tool;
	//private Properties m_config;
	private String m_context;
	//private String m_title;

	/**
	 * Construct
	 */
	public PlacementMock()
	{
	}

	/**
	 * Construct.
	 *
	 * @param id
	 *        The placement id.
	 * @param toolId
	 *        The well-known tool id associated with this placement.
	 * @param tool
	 *        The tool to place.
	 * @param config
	 *        The particular placement config Properties to use.
	 * @param context
	 *        The particular placement context to use.
	 * @param title
	 *        The tool placement title.
	 */
	public PlacementMock(String id, String toolId, Tool tool, Properties config, String context, String title)
	{
		m_id = id; 
		//m_toolId = toolId;// TODO
		//m_tool = tool;// TODO
		if (config != null)
		{
			//m_config = config;// TODO
		}
		m_context = context;
		//m_title = title;// TODO
	}
	

	public Properties getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContext() {
		
		return m_context;
	}

	public String getId() {
		return m_id;
	}

	public Properties getPlacementConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public Tool getTool() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void save() {
		// TODO Auto-generated method stub

	}

	public void setTitle(String arg0) {
		// TODO Auto-generated method stub

	}

	public void setTool(String arg0, Tool arg1) {
		// TODO Auto-generated method stub

	}

}
