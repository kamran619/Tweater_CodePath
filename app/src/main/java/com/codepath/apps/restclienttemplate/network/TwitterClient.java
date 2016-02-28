package com.codepath.apps.restclienttemplate.network;

import android.content.Context;

import com.codepath.apps.restclienttemplate.utils.Utils;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "FdGUxO7E7kTlaYH6ivj8IDv6R";       // Change this
	public static final String REST_CONSUMER_SECRET = "gJGpZrjk8R8euUvSl0fkrsyJ49439Waa4X5RqyE5Y3Jn7MHK0T"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cptweater"; // Change this (here and in manifest)

	private boolean isMocking;
	private Context context;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
		this.context = context;
		isMocking = false;
	}

	public void setMocking(boolean isMocking) {
		this.isMocking = isMocking;
	}

	public void getHomeTimeline(long fromLastId, AsyncHttpResponseHandler handler) {
		if (isMocking) {
			String mockedResponse = getMockedHomeTimelineResponse();
			try {
				JSONArray jsonArray = new JSONArray(mockedResponse);
				JsonHttpResponseHandler castedHandler = (JsonHttpResponseHandler) handler;
				castedHandler.onSuccess(200, null, jsonArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", fromLastId);
		getClient().get(apiUrl, params, handler);
	}

	private String getMockedHomeTimelineResponse() {
		return Utils.jsonToStringFromAssetFolder(context, "home_timeline.json");
	}

	public void getUserTimeline(long userId, long sinceId, AsyncHttpResponseHandler handler) {
		if (isMocking) {
			String mockedResponse = getMockedUserTimelineResponse();
			try {
				JSONArray jsonArray = new JSONArray(mockedResponse);
				JsonHttpResponseHandler castedHandler = (JsonHttpResponseHandler) handler;
				castedHandler.onSuccess(200, null, jsonArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 100);
		if (userId != 0) {
			params.put("user_id", userId);
		}
		params.put("since_id", sinceId);
		getClient().get(apiUrl, params, handler);
	}

	private String getMockedUserTimelineResponse() {
		return Utils.jsonToStringFromAssetFolder(context, "user_timeline.json");
	}

	public void getMentionsTimeline(long fromLastId, AsyncHttpResponseHandler handler) {
		if (isMocking) {
			String mockedResponse = getMockedMentionsTimelineResponse();
			try {
				JSONArray jsonArray = new JSONArray(mockedResponse);
				JsonHttpResponseHandler castedHandler = (JsonHttpResponseHandler) handler;
				castedHandler.onSuccess(200, null, jsonArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", fromLastId);
		getClient().get(apiUrl, params, handler);
	}

	private String getMockedMentionsTimelineResponse() {
		return Utils.jsonToStringFromAssetFolder(context, "mentions_timeline.json");
	}

	public void postTweet(String body, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", body);
		getClient().post(apiUrl, params, handler);
	}

	public void getCurrentUser(AsyncHttpResponseHandler handler) {
		if (isMocking) {
			String mockedResponse = getMockedCurrentUserResponse();
			try {
				JSONObject jsonObject = new JSONObject(mockedResponse);
				JsonHttpResponseHandler castedHandler = (JsonHttpResponseHandler) handler;
				castedHandler.onSuccess(200, null, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		String apiUrl = getApiUrl("account/verify_credentials.json");
		RequestParams params = new RequestParams();
		getClient().get(apiUrl, params, handler);
	}

	private String getMockedCurrentUserResponse() {
		return Utils.jsonToStringFromAssetFolder(context, "current_user.json");
	}

	public void getUser(long userId, AsyncHttpResponseHandler handler) {
		if (isMocking) {
			String mockedResponse = getMockedUserResponse();
			try {
				JSONObject jsonObject = new JSONObject(mockedResponse);
				JsonHttpResponseHandler castedHandler = (JsonHttpResponseHandler) handler;
				castedHandler.onSuccess(200, null, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("user_id", userId);
		getClient().get(apiUrl, params, handler);
	}

	private String getMockedUserResponse() {
		return Utils.jsonToStringFromAssetFolder(context, "current_user.json");
	}

	public void favorite(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, handler);
	}

	public void unfavorite(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, handler);
	}

	public void retweet(long id, AsyncHttpResponseHandler handler) {
		String url = "statuses/retweet/" + id + ".json";
		String apiUrl = getApiUrl(url);
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().post(apiUrl, params, handler);
	}
}