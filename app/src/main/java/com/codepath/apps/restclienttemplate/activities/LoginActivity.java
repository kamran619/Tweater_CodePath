package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.UserModel;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.codepath.apps.restclienttemplate.services.TwitterService;
import com.codepath.apps.restclienttemplate.utils.SharedPrefsHelper;
import com.codepath.oauth.OAuthLoginActionBarActivity;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupMocking();
	}

	private void setupMocking() {
		TwitterService.getInstance().setMocking(true);
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		TwitterService.getInstance().getCurrentUser(new TwitterService.IOnCurrentUserReceived() {
			@Override
			public void onSuccess(UserModel user) {
				SharedPrefsHelper.setCurrentUser(getBaseContext(), user);
			}

			@Override
			public void onFailure(String error) {
				Toast.makeText(getApplication(), "Error fetching current user blob", Toast.LENGTH_SHORT).show();
			}
		});
		Intent i = new Intent(getBaseContext(), TimelineActivity.class);
		startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().setRequestIntentFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getClient().connect();
	}

}
