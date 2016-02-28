package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kpirwani on 2/21/16.
 */
public class UserModel {
    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        if (hasLargeProfilePicture()) {
            return getLargeProfileImageUrl();
        } else {
            return getDefaultProfileImageUrl();
        }
    }

    private boolean hasLargeProfilePicture() {
        return getDefaultProfileImageUrl().indexOf("_large") != -1;
    }

    public String getLargeProfileImageUrl() {
        String largeProfileImageUrl = profileImageUrl;


        return largeProfileImageUrl;
    }

    public String getDefaultProfileImageUrl() {
        return profileImageUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    private String name;
    private  long uid;
    private String screenName;
    private String profileImageUrl;
    private String tagline;
    private int followerCount;
    private int followingCount;

    public static UserModel fromJSON(JSONObject jsonObject) {
        UserModel u = new UserModel();
        try {
            u.name = jsonObject.getString("name");
            u.uid = jsonObject.getLong("id");
            u.screenName = jsonObject.getString("screen_name");
            u.profileImageUrl = jsonObject.getString("profile_image_url");
            u.tagline = jsonObject.getString("description");
            u.followerCount = jsonObject.getInt("followers_count");
            u.followingCount = jsonObject.getInt("friends_count");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

}
