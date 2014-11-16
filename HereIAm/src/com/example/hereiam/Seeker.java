package com.example.hereiam;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Seeker extends Activity implements OnClickListener {

	Button ping, exit;
	TextView location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seeker);

		ping = (Button) findViewById(R.id.seeker_ping_button);
		location = (TextView) findViewById(R.id.seeker_location);
		exit = (Button) findViewById(R.id.seeker_exit);

		ping.setOnClickListener(this);
		exit.setOnClickListener(this);

		// get name of current hider
		ArrayList<NameValuePair> scriptParams = new ArrayList<NameValuePair>();
		new PostRequester("gethidername", scriptParams, new SuccessCallback() {

			@Override
			public void onRequesterResponse(boolean success) {
			}
		}).execute();

		// tell who the seeker is
		// new PopupFragment("The Hider is "+hider).show(getFragmentManager(),
		// "hiderr_notification");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == ping.getId()) {
			// request user location
			ArrayList<NameValuePair> scriptParams = new ArrayList<NameValuePair>();
			new PostRequester("gethiderloc", scriptParams, new SuccessCallback() {

				@Override
				public void onRequesterResponse(boolean success) {
					if (success) {

					} else {

					}

				}
			}).execute();
		} else if (v.getId() == exit.getId()) {
			// exit the user from the game

			ArrayList<NameValuePair> scriptParams = new ArrayList<NameValuePair>();
			scriptParams.add(new BasicNameValuePair("codename", MainActivity.getCodeName()));
			new PostRequester("exit", scriptParams, null).execute();
		}
	}
}