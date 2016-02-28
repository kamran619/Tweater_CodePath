package com.codepath.apps.restclienttemplate.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.UserModel;
import com.codepath.apps.restclienttemplate.utils.SharedPrefsHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComposeTweetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComposeTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeTweetFragment extends android.support.v4.app.DialogFragment {

    private final int MAX_CHARACTER_COUNT = 140;

    @Bind(R.id.etBody) EditText etBody;
    @Bind(R.id.tvRemaining) TextView tvRemaining;
    @Bind(R.id.tvComposeName) TextView tvName;
    @Bind(R.id.tvComposeHandle) TextView tvHandle;
    @Bind(R.id.ivComposeAvatar) ImageView ivAvatar;
    @Bind(R.id.btnTweet) Button btnTweet;
    public ITweetSubmit onTweetSubmit;

    final static private String TWEET_MAX_LIMIT_ERROR = "The tweet has surpassed the character limit of 140";
    final static private String TWEET_EMPTY = "The tweet must have some content in there";

    public static final String TWEET_TO = "TWEET_TO_NAME";
    public static final String TWEET_TO_ID = "TWEET_TO_ID";

    private String mTweetToName;
    private long mTweetToId;

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    public static ComposeTweetFragment newInstance(String tweetTo, long tweetToId) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putString(TWEET_TO, tweetTo);
        args.putLong(TWEET_TO_ID, tweetToId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTweetToName = getArguments().getString(TWEET_TO);
            mTweetToId = getArguments().getLong(TWEET_TO_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_compose, container, false);
        ButterKnife.bind(this, view);
        initCallback();
        initView();
        return view;
    }

    private void initCallback() {
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            validateParentFragmentConformsToInterface();
        } else {
            validateParentActivityConformstoInterface();
        }
    }

    private void validateParentFragmentConformsToInterface() {
        Fragment fragment = getParentFragment();
            if (fragment instanceof ITweetSubmit) {
                onTweetSubmit = (ITweetSubmit) fragment;
            } else {
                throw new RuntimeException(fragment.toString() + " must implement ITweetSubmit");
            }
    }

    private void validateParentActivityConformstoInterface() {
        Activity activity = getActivity();
        if (activity instanceof ITweetSubmit) {
            onTweetSubmit = (ITweetSubmit) activity;
        } else {
            throw new RuntimeException(activity.toString() + " must implement ITweetSubmit");
        }
    }

    private void initView() {
        UserModel user = SharedPrefsHelper.getCurrentUser(getActivity().getBaseContext());
        tvName.setText(user.getName());
        tvHandle.setText("@" + user.getScreenName());
        Glide.with(getActivity().getBaseContext()).load(user.getLargeProfileImageUrl()).into(ivAvatar);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweet = etBody.getText().toString();
                if (isTweetValid(tweet)) {
                    sendTweet(tweet);
                } else {
                    Toast.makeText(getActivity().getBaseContext(), getTweetError(tweet), Toast.LENGTH_SHORT).show();
                }

            }
        });
        if (isReplyTweet()) {
            getDialog().setTitle("Reply");
            etBody.setText("@" + mTweetToName + " ");
            etBody.setSelection(etBody.getText().length());
        } else {
            getDialog().setTitle("Compose a Tweet");
        }
    }

    private boolean isReplyTweet() {
        return mTweetToName != null && mTweetToId != 0;
    }

    private boolean isTweetValid(String tweet) {
        return (tweet.length() > 0 && tweet.length() < MAX_CHARACTER_COUNT);
    }

    private String getTweetError(String tweet) {
        if (tweet.length() <= 0) {
            return TWEET_EMPTY;
        } else {
            return TWEET_MAX_LIMIT_ERROR;
        }
    }
    private void sendTweet(String tweet) {
        if (onTweetSubmit != null) {
            onTweetSubmit.onTweetSubmit(tweet);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onTweetSubmit = null;
    }

    @OnTextChanged(R.id.etBody)
    void onTextChanged(CharSequence text) {
        final int currLength = text.length();
        final int remainingLength = MAX_CHARACTER_COUNT - currLength;
        tvRemaining.setText(remainingLength + " characters remaining");
        if (remainingLength > 0) {
            tvRemaining.setTextColor(Color.parseColor("#B6B6B6"));
        } else {
            tvRemaining.setTextColor(Color.RED);
        }
    }

    public interface ITweetSubmit {
        void onTweetSubmit(String tweet);
    }
}
