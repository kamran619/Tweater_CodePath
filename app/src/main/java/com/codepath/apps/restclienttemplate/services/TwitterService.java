package com.codepath.apps.restclienttemplate.services;

import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.apps.restclienttemplate.models.UserModel;
import com.codepath.apps.restclienttemplate.network.TwitterApplication;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kpirwani on 2/21/16.
 */
public class TwitterService {
    private TwitterClient mClient;

    private static TwitterService sharedInstance;

    private static final long FETCH_ALL = 1;
    private static final long CURRENT_USER_TIMELINE = 0;

    private long mLastTweetIdFetched = 0;
    private long mLastMentionIdFetched = 0;
    private long mLastUserTimelineTweetIdFetched = 0;

    private TwitterService() {
        mClient = TwitterApplication.getRestClient();
    }

    public static TwitterService getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new TwitterService();
        }
        return sharedInstance;
    }

    public void setMocking(boolean isMocking) {
        mClient.setMocking(isMocking);
    }

    public void getHomeTimeline(boolean fetchStartingFromLastId, final IOnHomeTimelineReceived callback) {
        long sinceId = (fetchStartingFromLastId == true) ? mLastTweetIdFetched : FETCH_ALL;
        mClient.getHomeTimeline(sinceId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                ArrayList<TweetModel> tweets = TweetModel.fromJSONArray(jsonArray);
                mLastTweetIdFetched = tweets.get(tweets.size() - 1).getUid();
                callback.onSuccess(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                callback.onFailure(errorResponse.toString());
            }
        });
    }

    public void getMentionsTimeline(boolean fetchStartingFromLastId, final IOnMentionsTimelineReceived callback) {
        long sinceId = (fetchStartingFromLastId == true) ? mLastMentionIdFetched : FETCH_ALL;
        mClient.getMentionsTimeline(sinceId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                ArrayList<TweetModel> tweets = TweetModel.fromJSONArray(jsonArray);
                mLastMentionIdFetched = tweets.get(tweets.size() - 1).getUid();
                callback.onSuccess(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                callback.onFailure(errorResponse.toString());
            }
        });
    }

    public void getCurrentUserTimeline(boolean fetchFromLastId, final IOnUserTimelineReceived callback) {
        getUserTimeline(CURRENT_USER_TIMELINE, fetchFromLastId, callback);
    }

    public void getUserTimeline(long userId, boolean fetchStartingFromLastId, final IOnUserTimelineReceived callback) {
        long sinceId = FETCH_ALL;
        if (fetchStartingFromLastId == false) {
            mLastUserTimelineTweetIdFetched = 1;
        } else {
            sinceId = mLastUserTimelineTweetIdFetched;
        }
        mClient.getUserTimeline(userId, sinceId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                ArrayList<TweetModel> tweets = TweetModel.fromJSONArray(jsonArray);
                mLastUserTimelineTweetIdFetched = tweets.get(tweets.size() - 1).getUid();
                callback.onSuccess(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                callback.onFailure(errorResponse.toString());
            }
        });
    }

    public void getCurrentUser(final IOnCurrentUserReceived callback) {
        mClient.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                UserModel user = UserModel.fromJSON(jsonObject);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                callback.onFailure(errorResponse.toString());
            }
        });
    }

    public void getUser(long userId, final IOnUserReceived callback) {
        mClient.getUser(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                UserModel user = UserModel.fromJSON(jsonObject);
                callback.onSuccess(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorObject) {
                callback.onFailure(errorObject.toString());
            }
        });
    }

    public void postTweet(String body, final IOnTweetPosted callback) {
        mClient.postTweet(body, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                TweetModel tweet = TweetModel.fromJSONObject(jsonObject);
                callback.onSuccess(tweet);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                callback.onFailure(errorResponse.toString());
            }
        });
    }

    public void favoriteTweet(long uid, final IOnTweetFavorited callback) {
        mClient.favorite(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                callback.onFailure(errorResponse.toString());
            }
        });
    }

    public void unfavoriteTweet(long uid, final IOnTweetFavorited callback) {
        mClient.unfavorite(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                callback.onFailure(errorResponse.toString());
            }
        });
    }

    public void retweetTweet(long uid, final IOnTweetRetweeted callback) {
        mClient.retweet(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                callback.onFailure(errorResponse.toString());
            }
        });
    }

    public interface IOnTweetPosted {
        public void onSuccess(TweetModel tweet);
        public void onFailure(String error);
    }

    public interface IOnCurrentUserReceived {
        public void onSuccess(UserModel user);
        public void onFailure(String error);
    }

    public interface IOnUserReceived {
        public void onSuccess(UserModel user);
        public void onFailure(String error);
    }

    public interface IOnHomeTimelineReceived {
        public void onSuccess(ArrayList<TweetModel> tweets);
        public void onFailure(String error);
    }

    public interface IOnMentionsTimelineReceived {
        public void onSuccess(ArrayList<TweetModel> tweets);
        public void onFailure(String error);
    }

    public interface IOnUserTimelineReceived {
        public void onSuccess(ArrayList<TweetModel> tweets);
        public void onFailure(String error);
    }

    public interface IOnTweetFavorited {
        public void onSuccess();
        public void onFailure(String error);
    }

    public interface IOnTweetRetweeted {
        public void onSuccess();
        public void onFailure(String error);
    }

}
