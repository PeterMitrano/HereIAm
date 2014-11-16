package com.example.hereiam;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class JoinGame extends Activity implements OnClickListener, SuccessCallback, OnDismissListener {

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
			joinGame();
		}
	}

	private void joinGame() {
		// join_game
		ArrayList<NameValuePair> scriptParams = new ArrayList<NameValuePair>();
		scriptParams.add(new BasicNameValuePair("gameName", game.getText().toString()));
		scriptParams.add(new BasicNameValuePair("seekerAlias", MainActivity.getAlias()));
		new PostRequester("join_game", scriptParams, "looking for games...", this, getFragmentManager()).runOnce();
	}

	@Override
	public void onRequesterResponse(boolean success) {
		if (success) {
			new PopupFragment("Aaaaand GO!!!", this).show(getFragmentManager(), "game_found");
		} else {
			new PopupFragment("There aren't any games with that name!").show(getFragmentManager(), "no_game_found");
		}

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		Intent seeker = new Intent(this, Seeker.class);
		startActivity(seeker);
	}
}