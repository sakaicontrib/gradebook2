package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;

public class Resource {

	private static final Log log = LogFactory.getLog(Resource.class);
	
	protected Gradebook2ComponentService service;
	
	protected <X> X fromJson(String text, Class<?> type) {
		X o = null;
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			o = (X)mapper.readValue(text, type);
		} catch (Exception e) {
			log.error("Caught an exception deserializing from JSON: " + text, e);
		}
		
		return o;
	}
	
	protected String toJson(List<?> list, int size) {
		return toJson(list, size, false); 
	}
	
	protected String toJson(List<?> list, int size, boolean pretty) 
	{
		Map<String,Object> wrapper = new HashMap<String, Object>();
		wrapper.put(AppConstants.LIST_ROOT, list);
		wrapper.put(AppConstants.TOTAL, String.valueOf(size));
		
		return toJson(wrapper, pretty);
		
	}
	protected String toJson(Object o)
	{
		return toJson(o, false); 
	}
	protected String toJson(Object o, boolean pretty) {
		ObjectMapper mapper = new ObjectMapper();
		
		if (pretty)
		{
			mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true); 
		}

		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, o);
		} catch (Exception e) {
			log.error("Caught an exception serializing to JSON: ", e);
		}
		
		return w.toString();
	}
	
	protected void logJsonToFile(List<?> list, int s, String outfile)
	{
		String jsonData; 
		jsonData = toJson(list, s, true); 
		logJsonToFileActual(jsonData, outfile); 
	}
	
	protected void logJsonToFile(Object o, String outfile) 
	{
		String jsonData; 
		jsonData = toJson(o, true); 
		logJsonToFileActual(jsonData, outfile);
	}
	private void logJsonToFileActual(String s, String outfile) 
	{
		File f = new File(outfile);
		boolean isDeleted = f.delete(); 

		if(!isDeleted) {
			log.error("Was not able to delete file = " + f.getName());
		}
		
		PrintWriter out;
		try {
			out = new PrintWriter(f);
			out.write(s);
			out.flush();
			out.close(); 
		} catch (FileNotFoundException e) {
			log.warn("Caught exception: " + e, e); 
		} 

	}
	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}
	
}
