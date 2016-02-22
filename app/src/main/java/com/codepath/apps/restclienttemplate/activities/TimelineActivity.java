package com.codepath.apps.restclienttemplate.activities;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetsArrayAdapter;
import com.codepath.apps.restclienttemplate.services.TwitterService;
import com.codepath.apps.restclienttemplate.fragments.ComposeTweetFragment;
import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.apps.restclienttemplate.uiTweaks.SimpleDividerItemDecoration;
import com.codepath.apps.restclienttemplate.utils.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.OnFragmentInteractionListener, ComposeTweetFragment.ITweetSubmit, TweetsArrayAdapter.ITweetActionListener {

    private ArrayList<TweetModel> tweets;
    private TweetsArrayAdapter aTweets;
    @Bind(R.id.rvTimeline)
    RecyclerView rvTimeline;

    private ComposeTweetFragment composeTweetFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
        android.support.v7.app.ActionBar myToolbar = getSupportActionBar();
        myToolbar.setTitle("Activity Timeline");
        ButterKnife.bind(this);
        setupModel();
        setupAdapter();
        setupRecyclerView();
        populateTimeline(false);
    }

    private void setupModel() {
        tweets = new ArrayList<>();
    }

    private void setupAdapter() {
        aTweets = new TweetsArrayAdapter(getApplication(), tweets, this);
    }

    private void setupRecyclerView() {
        rvTimeline.setAdapter(aTweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvTimeline.setLayoutManager(layoutManager);
        rvTimeline.setItemAnimator(new SlideInUpAnimator());
        rvTimeline.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline(true);
            }
        });
        ItemClickSupport.addTo(rvTimeline).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

            }
        });
        rvTimeline.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
    }

    private void populateTimeline(boolean fetchFromLastTweet) {
        TwitterService.getInstance().getHomeTimeline(fetchFromLastTweet, new TwitterService.IOnHomeTimelineReceived() {
            @Override
            public void onSuccess(ArrayList<TweetModel> tweetsList) {
                tweets.addAll(tweetsList);
                int curSize = aTweets.getItemCount();
                aTweets.notifyItemRangeChanged(curSize, tweets.size() - 1);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void launchComposeFragment(View v) {
        composeTweetFragment = new ComposeTweetFragment();
        composeTweetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogTheme);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        composeTweetFragment.show(ft, "");
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTweetSubmit(String tweet) {
        composeTweetFragment.dismiss();
        TwitterService.getInstance().postTweet(tweet, new TwitterService.IOnTweetPosted() {
            @Override
            public void onSuccess(TweetModel tweet) {
                tweets.add(0, tweet);
                aTweets.notifyItemInserted(0);
                rvTimeline.scrollToPosition(0);
                Toast.makeText(getBaseContext(), "Tweeted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void retweet(final TweetModel tweetModel) {
        TwitterService.getInstance().retweetTweet(tweetModel.getUid(), new TwitterService.IOnTweetRetweeted() {
            @Override
            public void onSuccess() {
                Toast.makeText(getBaseContext(), "Retweeted!", Toast.LENGTH_SHORT).show();
                tweetModel.setRetweeted(true);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void reply(TweetModel tweetModel) {
        composeTweetFragment = ComposeTweetFragment.newInstance(tweetModel.getUser().getScreenName(), tweetModel.getUid());
        composeTweetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogTheme);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        composeTweetFragment.show(ft, "");
    }

    @Override
    public void favorite(final TweetModel tweetModel) {
        TwitterService.getInstance().favoriteTweet(tweetModel.getUid(), new TwitterService.IOnTweetFavorited() {
            @Override
            public void onSuccess() {
                Toast.makeText(getBaseContext(), "Favorited!", Toast.LENGTH_SHORT).show();
                tweetModel.setFavorited(true);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void unfavorite(final TweetModel tweetModel) {
        TwitterService.getInstance().unfavoriteTweet(tweetModel.getUid(), new TwitterService.IOnTweetFavorited() {
            @Override
            public void onSuccess() {
                Toast.makeText(getBaseContext(), "Unfavorited!", Toast.LENGTH_SHORT).show();
                tweetModel.setFavorited(false);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
