package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.apps.restclienttemplate.services.TwitterService;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kpirwani on 2/27/16.
 */
public class UserTimelineFragment extends TweetsListFragment {

    public static final String USER_ID = "userId";
    public static final String PROFILE_CLICKABLE = "profileClickable";
    public static final String SHOWS_FLOATING_ACTION_BUTTON = "showsFloatingActionButton";

    private long mUserId;

    @Bind(R.id.pbUserTimeline)
    ProgressBar pbUserTimeline;

    public static UserTimelineFragment newInstance(long userId, boolean isProfileClickable, boolean showsFloatingActionButton) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(USER_ID, userId);
        bundle.putBoolean(PROFILE_CLICKABLE, isProfileClickable);
        bundle.putBoolean(SHOWS_FLOATING_ACTION_BUTTON, showsFloatingActionButton);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupArguments();
        setupEndlessScrolling();
        populateTimeline(false);
    }

    private void setupArguments() {
        if (getArguments() != null) {
            mUserId = getArguments().getLong(USER_ID);
            super.setIsProfilePictureClickable(getArguments().getBoolean(PROFILE_CLICKABLE));
            super.setShowsFloatingActionButton(getArguments().getBoolean(SHOWS_FLOATING_ACTION_BUTTON));
        }
    }

    private void setupEndlessScrolling() {
        rvTimeline.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager)rvTimeline.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(true);
            }
        });
    }

    public void populateTimeline(boolean fetchFromLastTweet) {
        TwitterService.getInstance().getUserTimeline(mUserId, fetchFromLastTweet, new TwitterService.IOnUserTimelineReceived() {
            @Override
            public void onSuccess(ArrayList<TweetModel> tweetsList) {
                pbUserTimeline.setVisibility(View.GONE);
                UserTimelineFragment.super.addAll(tweetsList);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
