package com.codepath.apps.restclienttemplate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ProfileActivity;
import com.codepath.apps.restclienttemplate.adapters.TweetsArrayAdapter;
import com.codepath.apps.restclienttemplate.models.TweetModel;
import com.codepath.apps.restclienttemplate.services.TwitterService;
import com.codepath.apps.restclienttemplate.uiTweaks.SimpleDividerItemDecoration;
import com.codepath.apps.restclienttemplate.utils.ItemClickSupport;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TweetsList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TweetsList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TweetsListFragment extends Fragment implements ComposeTweetFragment.ITweetSubmit, TweetsArrayAdapter.ITweetActionListener {

    private ArrayList<TweetModel> tweets;
    private TweetsArrayAdapter aTweets;
    @Bind(R.id.rvTimeline)
    RecyclerView rvTimeline;

    @Bind(R.id.fabCompose)
    FloatingActionButton btnCompose;
    public boolean isProfilePictureClickable() {
        return mIsProfilePictureClickable;
    }

    public void setIsProfilePictureClickable(boolean mIsProfilePictureClickable) {
        this.mIsProfilePictureClickable = mIsProfilePictureClickable;
    }

    protected boolean mIsProfilePictureClickable;

    public boolean isShowingFloatingActionButton() {
        return mShowsFloatingActionButton;
    }

    public void setShowsFloatingActionButton(boolean mShowsFloatingActionButton) {
        this.mShowsFloatingActionButton = mShowsFloatingActionButton;
        if (mShowsFloatingActionButton == true) {
            btnCompose.setVisibility(View.VISIBLE);
        } else {
            btnCompose.setVisibility(View.GONE);
        }
    }

    protected boolean mShowsFloatingActionButton;

    private ComposeTweetFragment composeTweetFragment;

    public TweetsListFragment() {
        // Required empty public constructor
    }

    public static TweetsListFragment newInstance() {
        TweetsListFragment fragment = new TweetsListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, view);
        setupModel();
        setupAdapter();
        setupRecyclerView();
        setupActions();
        return view;
    }

    private void setupActions() {
        mIsProfilePictureClickable = true;
    }

    private void setupModel() {
        tweets = new ArrayList<>();
    }

    private void setupAdapter() {
        aTweets = new TweetsArrayAdapter(getActivity(), tweets, this);
    }

    private void setupRecyclerView() {
        rvTimeline.setAdapter(aTweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvTimeline.setLayoutManager(layoutManager);
        rvTimeline.setItemAnimator(new SlideInUpAnimator());

        ItemClickSupport.addTo(rvTimeline).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

            }
        });
        rvTimeline.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
    }

    protected void addAll(ArrayList<TweetModel> tweetsList) {
        tweets.addAll(tweetsList);
        int curSize = aTweets.getItemCount();
        aTweets.notifyItemRangeChanged(curSize, tweets.size() - 1);
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
                Toast.makeText(getContext(), "Tweeted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void retweet(final TweetModel tweetModel) {
        TwitterService.getInstance().retweetTweet(tweetModel.getUid(), new TwitterService.IOnTweetRetweeted() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Retweeted!", Toast.LENGTH_SHORT).show();
                tweetModel.setRetweeted(true);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void reply(TweetModel tweetModel) {
        composeTweetFragment = ComposeTweetFragment.newInstance(tweetModel.getUser().getScreenName(), tweetModel.getUid());
        composeTweetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogTheme);
        composeTweetFragment.show(getChildFragmentManager(), "");
    }

    @Override
    public void profile(TweetModel tweetModel) {
        if (isProfilePictureClickable()) {
            Intent i = new Intent(getActivity(), ProfileActivity.class);
            i.putExtra(ProfileActivity.USER_ID, tweetModel.getUser().getUid());
            i.putExtra(ProfileActivity.USER_HANDLE, tweetModel.getUser().getScreenName());
            startActivity(i);
        }
    }

    @OnClick(R.id.fabCompose)
    void onComposeButtonClicked(View v) {
        launchComposeFragment();
    }

    public void launchComposeFragment() {
        composeTweetFragment = new ComposeTweetFragment();
        composeTweetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialogTheme);
        composeTweetFragment.show(getChildFragmentManager(), "");
    }

    @Override
    public void favorite(final TweetModel tweetModel) {
        TwitterService.getInstance().favoriteTweet(tweetModel.getUid(), new TwitterService.IOnTweetFavorited() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Favorited!", Toast.LENGTH_SHORT).show();
                tweetModel.setFavorited(true);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void unfavorite(final TweetModel tweetModel) {
        TwitterService.getInstance().unfavoriteTweet(tweetModel.getUid(), new TwitterService.IOnTweetFavorited() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Unfavorited!", Toast.LENGTH_SHORT).show();
                tweetModel.setFavorited(false);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
