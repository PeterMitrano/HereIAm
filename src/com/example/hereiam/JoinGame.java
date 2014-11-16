package com.example.hereiam;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinGame extends Activity implements OnClickListener, RequesterCallback {

	private Button join;
	private EditText game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_game);

		join = (Button) findViewById(R.id.join_game_join_button);
		game = (EditText) findViewById(R.id.join_game_name);

		join.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == join.getId()) {
			// start join game dialog
			if (game.getText().length() != 5) {
				Toast.makeText(this, "game names are 5 characters long", Toast.LENGTH_LONG).show();
			} else {
				joinGame();
			}
		}
	}

	private void joinGame() {
		String gameName = game.getText().toString();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String codeName = sharedPreferences.getString(getString(R.string.codename), getString(R.string.codename));

		// setup parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("gamename", gameName));
		params.add(new BasicNameValuePair("codename", codeName));

		// make request to server
		new Requester("joingame", params, "Joining Game...", this, getFragmentManager()).execute();

	}

	@Override
	public void onRequesterResponse(boolean success) {
		if (success) {
			// successfully added game on server
			Log.d("start_status", "successfully called script");
			Intent seeker = new Intent(this, Seeker.class);
			startActivity(seeker);
		} else {
			// failed to add game on server
			Log.d("start_status", "failed to call script");
			new RequestFragment("There aren't any games with that name!").show(getFragmentManager(), "no_game_found");
		}

	}
}