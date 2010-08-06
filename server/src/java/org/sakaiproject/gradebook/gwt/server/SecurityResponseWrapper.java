package org.sakaiproject.gradebook.gwt.server;

import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class SecurityResponseWrapper extends HttpServletResponseWrapper {

	private ByteArrayOutputStream output;

	public SecurityResponseWrapper(HttpServletResponse response) {
		super(response);
		output = new ByteArrayOutputStream();
	}

	public ServletOutputStream getOutputStream() {
		return new FilterServletOutputStream(output);
	}
	
	public byte[] getData() {
		return output.toByteArray();
	}
	
	public String toString() {
		return output.toString();
	}
}
