package org.sakaiproject.gradebook.gwt.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

public class FilterServletOutputStream extends ServletOutputStream {

	private DataOutputStream stream;

	public FilterServletOutputStream(OutputStream output) {

		stream = new DataOutputStream(output);
	}

	@Override
	public void write(int b) throws IOException {
		
		stream.write(b);
	}
}
