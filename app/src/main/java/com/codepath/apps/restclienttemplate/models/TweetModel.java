package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kpirwani on 2/21/16.
 */
public class TweetModel {
    private String body;
    private long uid; //unique item_tweet id
    private UserModel user;
    private String createdAt;

    private boolean favorited;
    private String location;

    private boolean retweeted;
    private int favoriteCount;
    private int retweetCount;
    private String imageUrl;

    public boolean isRetweeted() {
        return retweeted;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public UserModel getUser() {
        return user;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public String getLocation() {
        return location;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getUid() {
        return uid;
    }

    public String getBody() {
        return body;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public static ArrayList<TweetModel> fromJSONArray(JSONArray jsonArray) {
        ArrayList<TweetModel> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                TweetModel tweetModel = fromJSONObject(tweetJson);
                tweets.add(tweetModel);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }

    public static TweetModel fromJSONObject(JSONObject jsonObject) {
        TweetModel tweet = new TweetModel();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = new UserModel().fromJSON(jsonObject.getJSONObject("user"));
            tweet.favorited = jsonObject.optBoolean("favorited");
            tweet.location = jsonObject.optString("location");
            tweet.retweeted =  jsonObject.optBoolean("retweeted");
            tweet.retweetCount = jsonObject.optInt("retweet_count");
            tweet.favoriteCount = jsonObject.optInt("favorite_count");

            JSONObject entities = jsonObject.optJSONObject("entities");
            if (entities != null) {
                JSONArray media = entities.optJSONArray("media");
                if (media != null) {
                    tweet.imageUrl = media.optJSONObject(0).optString("media_url");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }
}
