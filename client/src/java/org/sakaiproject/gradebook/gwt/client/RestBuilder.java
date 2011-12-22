/**********************************************************************************
*
* Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client;

import java.util.EnumSet;

import org.sakaiproject.gradebook.gwt.client.gxt.EntityOverlayResultReader;
import org.sakaiproject.gradebook.gwt.client.gxt.NewModelCallback;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;
import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerModel;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.HttpProxy;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.JsonPagingLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RestBuilder extends RequestBuilder {

	public enum Method {
		
		GET, POST, PUT, DELETE
	};
	
	protected I18nConstants i18n = Registry.get(AppConstants.I18N);

	protected RestBuilder(String method, String url) {
		
		super(method, url);
	}

	public static RestBuilder getInstance(Method method, String url) {
		
		String header = null;
		switch (method) {
		case DELETE:
		case PUT:
			header = method.name();
			method = Method.POST;
			break;
		}
		RestBuilder builder = new RestBuilder(method.name(), url);

		if (header != null)
			builder.setHeader("X-HTTP-Method-Override", header);

		builder.setHeader("Content-Type", AppConstants.HEADER_CONTENT_TYPE_JSON_UTF8);
		String gb2Token = Cookies.getCookie(AppConstants.GB2_TOKEN);
		if (null == gb2Token || "".equals(gb2Token)) {
			builder.setHeader(AppConstants.X_XSRF_COOKIE, AppConstants.NO_VALUE_COOKIE);
		} else {
			builder.setHeader(AppConstants.X_XSRF_COOKIE, gb2Token);
		}

		return builder;
	}

	public static RestBuilder getInstance(Method method, String... urlArgs) {
		
		return getInstance(method, buildInitUrl(urlArgs));
	}

	public static <M extends ModelData> ListLoader<ListLoadResult<M>> getDelayLoader(
			String root, EnumSet<?> enumSet, Method method,
			UrlArgsCallback argsCallback, final NewModelCallback modelCallback,
			String... urlArgs) {
		
		HttpProxy<String> proxy = getProxy(urlArgs, argsCallback);

		ModelType type = new ModelType();
		type.setRoot(root);
		type.setTotalName(AppConstants.TOTAL);

		JsonLoadResultReader<ListLoadResult<M>> reader = new EntityOverlayResultReader<ListLoadResult<M>>(type) {

			protected ModelData newModelInstance(EntityOverlay overlay) {
				if (modelCallback == null)
					return new EntityModel(overlay);

				return modelCallback.newModelInstance(overlay);
			}

		};

		return new BaseListLoader<ListLoadResult<M>>(proxy, reader);
	}

	public static <M extends ModelData> PagingLoader<PagingLoadResult<M>> getPagingDelayLoader(
			Method method, final NewModelCallback modelCallback,
			String... urlArgs) {
		
		HttpProxy<String> proxy = getProxy(urlArgs, null);

		ModelType type = new ModelType();
		type.setRoot(AppConstants.LIST_ROOT);
		type.setTotalName(AppConstants.TOTAL);

		JsonPagingLoadResultReader<PagingLoadResult<M>> reader = new EntityOverlayResultReader<PagingLoadResult<M>>(
				type) {

			protected ModelData newModelInstance(EntityOverlay overlay) {
				if (modelCallback == null)
					return new EntityModel(overlay);
				return modelCallback.newModelInstance(overlay);
			}

		};
		return new BasePagingLoader<PagingLoadResult<M>>(proxy, reader);
	}

	public static <M extends ModelData> PagingLoader<PagingLoadResult<M>> getLearnerLoader(
			Method method, String... urlArgs) {
		
		HttpProxy<String> proxy = getProxy(urlArgs, null);

		ModelType type = new ModelType();
		type.setRoot(AppConstants.LIST_ROOT);
		type.setTotalName(AppConstants.TOTAL);

		JsonPagingLoadResultReader<PagingLoadResult<M>> reader = new EntityOverlayResultReader<PagingLoadResult<M>>(
				type) {

			protected ModelData newModelInstance(EntityOverlay overlay) {
				return new LearnerModel(overlay);
			}

		};

		return new BasePagingLoader<PagingLoadResult<M>>(proxy, reader);
	}

	public Request sendRequest(final int successCode, final int failureCode,
			String requestData, final RestCallback callback) {

		Request request = null;
		try {
			super.sendRequest(requestData, new RequestCallback() {

				public void onError(Request request, Throwable exception) {
					callback.onError(request, exception);
				}

				public void onResponseReceived(Request request,
						Response response) {
					if (response.getStatusCode() == successCode
							|| (GXT.isIE && response.getStatusCode() == 1223 && successCode == 204)) {

						callback.onSuccess(request, response);
					} else if (response.getStatusCode() == failureCode
							&& null != response.getText()
							&& !"".equals(response.getText())) {

						callback.onFailure(request,
								new Exception(response.getText()));
					} else { 
						// handle special status code cases
						if (null != response.getText()
								&& !"".equals(response.getText())) {
							switch (response.getStatusCode()) {
							case 409:
							case 500:
								callback.onError(request, new Exception(
										response.getStatusText() + " : "
												+ response.getStatusCode()), response.getStatusCode());
							default:
								callback.onError(request, new Exception(
										i18n.unexpectedResponseFromServer()
												+ response.getStatusCode()), response.getStatusCode());
							}
						} else {
							callback.onError(request, new Exception(
									i18n.unexpectedResponseFromServer()
											+ response.getStatusCode()), response.getStatusCode());
						}
					}
				}

			});
		} catch (RequestException re) {
			callback.onError(request, re);
		}
		return request;
	}

	public static String buildInitUrl(String... args) {
		
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < args.length; i++) {

			if(null == args[i]) {
				GWT.log("ERROR: encountered a NULL URL item.");
				continue;
			}
			
			builder.append(args[i]);

			if (!args[i].endsWith("/")) {
			
				builder.append("/");
			}
		}

		return builder.toString();
	}

	private static HttpProxy<String> getProxy(String[] urlArgs,
			final UrlArgsCallback argsCallback) {

		final String partialUrl = RestBuilder.buildInitUrl(urlArgs);
		RestBuilder builder = RestBuilder.getInstance(Method.GET, partialUrl);
		return new HttpProxy<String>(builder) {

			public void load(final DataReader<String> reader,
					final Object loadConfig,
					final AsyncCallback<String> callback) {
				Gradebook gbModel = Registry.get(AppConstants.CURRENT);

				if (argsCallback != null)
					
					initUrl = RestBuilder.buildInitUrl(partialUrl, gbModel
							.getGradebookUid(), String.valueOf(gbModel
							.getGradebookId()), argsCallback.getUrlArg());
				else
					initUrl = RestBuilder.buildInitUrl(partialUrl, gbModel
							.getGradebookUid(), String.valueOf(gbModel
							.getGradebookId()));

				super.load(reader, loadConfig, callback);
			}
		};
	}
}
