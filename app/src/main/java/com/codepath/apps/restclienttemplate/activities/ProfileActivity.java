package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.UserHeaderFragment;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;

public class ProfileActivity extends AppCompatActivity {

    public static final String USER_ID = "user_id";
    public static final String USER_HANDLE = "user_handle";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (savedInstanceState == null) {
            //Get the string name
            long userId = getIntent().getLongExtra(USER_ID, 0);

            android.support.v7.app.ActionBar myToolbar = getSupportActionBar();
            if (myToolbar != null) {
                myToolbar.setBackgroundDrawable(getResources().getDrawable(R.color.divider));
                String userHandle = "@" + getIntent().getStringExtra(USER_HANDLE);
                myToolbar.setTitle(userHandle);
            }
            //create user timeline fragment
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(userId, false, false);
            UserHeaderFragment userHeaderFragment = UserHeaderFragment.newInstance(userId);
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flUserHeader, userHeaderFragment);
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }

}
