package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.UserModel;
import com.codepath.apps.restclienttemplate.services.TwitterService;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserHeaderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserHeaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserHeaderFragment extends Fragment {

    @Bind(R.id.tvName)
    TextView tvName;
    @Bind(R.id.tvTagline)
    TextView tvTagline;
    @Bind(R.id.tvFollowers)
    TextView tvFollowers;
    @Bind(R.id.tvFollowing)
    TextView tvFollowing;
    @Bind(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @Bind(R.id.pbUserHeader)
    ProgressBar pbUserHeader;
    public static final String USER_ID = "USER_ID";
    public static final long CURRENT_USER = 0;

    private long mUserId;

    public UserHeaderFragment() {
        // Required empty public constructor
    }

    public static UserHeaderFragment newInstance(long userId) {
        UserHeaderFragment fragment = new UserHeaderFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_header, container, false);
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            mUserId = getArguments().getLong(USER_ID);
            fetchUserAndPopulateUI();
        }
        return view;
    }

    private void fetchUserAndPopulateUI() {
        if (mUserId == CURRENT_USER) {
            TwitterService.getInstance().getCurrentUser(new TwitterService.IOnCurrentUserReceived() {
                @Override
                public void onSuccess(UserModel user) {
                    populateHeaderFromModel(user);
                }

                @Override
                public void onFailure(String error) {

                }
            });

        } else {
            TwitterService.getInstance().getUser(mUserId, new TwitterService.IOnUserReceived() {
                @Override
                public void onSuccess(UserModel user) {
                    populateHeaderFromModel(user);
                }

                @Override
                public void onFailure(String error) {

                }
            });
        }
    }

    private void populateHeaderFromModel(UserModel user) {
        pbUserHeader.setVisibility(View.GONE);
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        tvFollowers.setText(user.getFollowerCount() + " followers");
        tvFollowing.setText(user.getFollowingCount() + " following");
        Glide.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);
    }
}
