/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.gradebook.gwt.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class SecurityRequestWrapper extends HttpServletRequestWrapper {

	private final static int MAX_INPUT_BUFFER_SIZE = 1024;

	private final String body;
	private int contentLength = 0;

	public SecurityRequestWrapper(HttpServletRequest request) throws IOException {

		super(request);

		StringBuilder stringBuilder = new StringBuilder();

		BufferedReader bufferedReader = null;

		try {

			InputStream inputStream = request.getInputStream();

			if (inputStream != null) {

				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

				contentLength = request.getContentLength();


				char[] charBuffer;

				// In case someone tampered with the contentLength
				if(contentLength < 1 || contentLength > MAX_INPUT_BUFFER_SIZE) {
					charBuffer = new char[MAX_INPUT_BUFFER_SIZE];
				}
				else {
					charBuffer = new char[contentLength];
				}

				int bytesRead = -1;

				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}

			} else {
				stringBuilder.append("");
			}
		}
		catch (IOException ex) {

			throw ex;
		}
		finally {

			if (bufferedReader != null) {

				try {

					bufferedReader.close();
				}
				catch (IOException ex) {
					throw ex;
				}
			}
		}

		body = stringBuilder.toString();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {

		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());

		ServletInputStream servletInputStream = new ServletInputStream() {

			public int read() throws IOException {

				return byteArrayInputStream.read();
			}
		};

		return servletInputStream;
	}

	@Override
	public BufferedReader getReader() throws IOException {

		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	public String getBody() throws ServletException {

		int bodySize = body.getBytes().length;

		if (bodySize != contentLength) {
			throw new ServletException("The request's and actual body content length do not match");
		}

		return body;
	}
}
