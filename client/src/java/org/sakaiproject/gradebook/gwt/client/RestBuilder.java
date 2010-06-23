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
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RestBuilder extends RequestBuilder {

	public enum Method {
		
		GET, POST, PUT, DELETE
	};

	protected RestBuilder(String method, String url) {
		
		super(method, url);
	}

	public static RestBuilder getInstance(Method method, String url) {
		
		String header = null;
		switch (method) {
		case DELETE:
		case PUT:
			// if (GXT.isSafari) {
			header = method.name();
			method = Method.POST;
			// }
			break;
		}
		RestBuilder builder = new RestBuilder(method.name(), url);

		if (header != null)
			builder.setHeader("X-HTTP-Method-Override", header);

		builder.setHeader("Content-Type", "application/json; charset=utf-8");
		String jSessionId = Cookies.getCookie("JSESSIONID");
		if (null == jSessionId || "".equals(jSessionId)) {
			builder.setHeader("X-XSRF-Cookie", "No-Cookie");
		} else {
			builder.setHeader("X-XSRF-Cookie", jSessionId);
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
							|| (GXT.isIE && response.getStatusCode() == 1223 && successCode == 204))
						callback.onSuccess(request, response);
					else if (response.getStatusCode() == failureCode)
						callback.onFailure(request, new Exception(response
								.getText()));
					else
						callback.onError(request, new Exception(
								"Unexpected response from server: "
										+ response.getStatusCode()));
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

			// The first argument is the server's URL, so we don't want to encode it
			if(0 == i) {
				builder.append(args[i]);
			}
			else {
				// All the other arguments need to be encoded
				builder.append(URL.encodeComponent(args[i]));
			}

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
