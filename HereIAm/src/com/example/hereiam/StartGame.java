package com.example.hereiam;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartGame extends Activity implements OnClickListener, SuccessCallback {

	private Button start;
	private TextView code;
	private static final String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String gameName;

	String[] names = { "yellow", "epic", "super", "fat", "tall", "anti", "smile", "bird", "bug", "ninja", "wormhole", "kiwi",
			"sushi", "burrito", "taco" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_game);

		start = (Button) findViewById(R.id.start_game_start_button);
		code = (TextView) findViewById(R.id.start_game_code);

		start.setOnClickListener(this);

		gameName = getAlphaNumeric(5);
		code.setText(gameName);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == start.getId()) {
			startGame();
		}
	}

	private void startGame() {

		// setup parameters
		List<NameValuePair> scriptParams = new ArrayList<NameValuePair>();
		scriptParams.add(new BasicNameValuePair("gameName", gameName));
		scriptParams.add(new BasicNameValuePair("hiderAlias", MainActivity.getAlias()));

		new PostRequester("start_game", scriptParams, "Starting a game...", this, getFragmentManager()).runOnce();

	}

	public String getAlphaNumeric(int len) {
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			int ndx = (int) (Math.random() * ALPHA_NUM.length());
			sb.append(ALPHA_NUM.charAt(ndx));
		}
		return sb.toString();
	}

	@Override
	public void onRequesterResponse(boolean success) {
		if (success) {
			Intent hider = new Intent(this, Hider.class);
			startActivity(hider);
		} else {
			Log.e("shit", "shit...coudn't make a game!");
		}
	}

}