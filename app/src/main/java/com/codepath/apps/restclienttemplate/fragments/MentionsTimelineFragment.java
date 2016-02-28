package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.apps.restclienttemplate.services.TwitterService;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

/**
 * Created by kpirwani on 2/27/16.
 */
public class MentionsTimelineFragment extends TweetsListFragment {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupEndlessScrolling();
        populateTimeline(false);
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
        TwitterService.getInstance().getMentionsTimeline(fetchFromLastTweet, new TwitterService.IOnMentionsTimelineReceived() {
            @Override
            public void onSuccess(ArrayList<TweetModel> tweetsList) {
                MentionsTimelineFragment.super.addAll(tweetsList);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
