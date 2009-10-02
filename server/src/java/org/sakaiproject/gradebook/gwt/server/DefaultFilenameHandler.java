package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.HttpRequestHandler;

public class DefaultFilenameHandler implements HttpRequestHandler {

	private static final int BUFFER_SIZE = 2048;
	
	private static Log log = LogFactory.getLog(DefaultFilenameHandler.class);
	
	
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String path = request.getPathInfo();
		
		if (path == null)
			path = request.getServletPath();
		
		if (path == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if (path.startsWith("//")) 
			path = path.replaceAll("//", "/");

		ServletContext servletContext = request.getSession().getServletContext();
		
		URL url = servletContext.getResource(path);
		
		if (url == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		URLConnection connection = url.openConnection();
		
		String contentType = connection.getContentType();
		
		if (contentType == null) {
			if (path.endsWith(".html") || path.contains(".html?")) {
				contentType = "text/html";
			} else if (path.endsWith(".css")) {
				contentType = "text/css";
			} else if (path.endsWith(".js")) {
				contentType = "text/javascript";
			} else if (path.endsWith(".gif")) {
				contentType = "image/gif";
			} else if (path.endsWith(".png")) {
				contentType = "image/png";
			} else if (path.endsWith(".jpg")) {
				contentType = "image/jpeg";
			}
		}
		
		int contentLength = connection.getContentLength();
		response.setContentLength(contentLength);
		response.setContentType(contentType);

		log.info("Path: " + path + " Content type: " + contentType);
		
		long lastModified = connection.getLastModified();
		String lastModifiedString = new Date(lastModified).toString();
		
		String eTag = new StringBuilder().append("W/\"")
			.append(contentLength).append("-").append(lastModified)
			.append("\"").toString();

		response.setHeader("ETag", eTag);
		response.setHeader("Last-Modified", lastModifiedString);
		
		InputStream resourceStream = connection.getInputStream();
		
		if (resourceStream == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		ServletOutputStream outputStream = response.getOutputStream();
		
		IOException exception = copyStream(resourceStream, outputStream);
		
		resourceStream.close();
		outputStream.close();
	
		if (exception != null)
			throw exception;
	}

	/*
	 * Code for this method copied from Apache Tomcat DefaultServlet, licensed under Apache 2.0:
	 * http://www.apache.org/licenses/LICENSE-2.0
	 */
	private IOException copyStream(InputStream input, ServletOutputStream output) {

		byte buffer[] = new byte[BUFFER_SIZE];
		
		IOException exception = null;
		
		int len = buffer.length;
		while (true) {
			try {
				len = input.read(buffer);
				if (len == -1)
					break;
				output.write(buffer, 0, len);
			} catch (IOException e) {
				exception = e;
				len = -1;
				break;
			}
		}
		return exception;
	}

	
}
