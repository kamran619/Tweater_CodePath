package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsTimelineFragment;
import com.codepath.apps.restclienttemplate.models.UserModel;
import com.codepath.apps.restclienttemplate.utils.SharedPrefsHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity {

    @Bind(R.id.viewpager)
    android.support.v4.view.ViewPager mViewPager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip mTabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        android.support.v7.app.ActionBar myToolbar = getSupportActionBar();
        myToolbar.setBackgroundDrawable(getResources().getDrawable(R.color.divider));
        myToolbar.setTitle("Activity Timeline");
        setupViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        mViewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        mTabs.setViewPager(mViewPager);
    }

    public void onProfileView(MenuItem mi) {
        Intent i = new Intent(this, ProfileActivity.class);
        UserModel user = SharedPrefsHelper.getCurrentUser(this);
        i.putExtra(ProfileActivity.USER_HANDLE, user.getScreenName());
        startActivity(i);
    }

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        final private String tabTitles[] = { "Home", "Mentions" };

        private HomeTimelineFragment mHomeTimelineFragment;
        private MentionsTimelineFragment mMentionsTimelineFragment;

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return createHomeTimelineFragment();
            } else if (position == 1) {
                return createMentionsTimelineFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        private HomeTimelineFragment createHomeTimelineFragment() {
            HomeTimelineFragment fragment = new HomeTimelineFragment();
            return fragment;
        }

        private MentionsTimelineFragment createMentionsTimelineFragment() {
            MentionsTimelineFragment fragment = new MentionsTimelineFragment();
            return fragment;
        }
    }
}
