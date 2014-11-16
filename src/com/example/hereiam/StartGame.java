package com.example.hereiam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StartGame extends Activity implements OnClickListener {

	private Button start;
	private TextView code;
	private static final String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	String gameName;

	String[] names = { "" };

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
			if (startGame()) {
				// successfully added game on server
				Log.d("start_status", "successfully created game");
			} else {
				// failed to add game on server
				Log.d("start_status", "failed to make game");
			}
		}
	}

	private boolean startGame() {
		boolean success = true;
		String codeName = "peter";
		postData(gameName, codeName);
		return success;
	}

	public void postData(String gameName, String codeName) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://sophocles.rmorey.me/hackrpi/startgame.php");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("gamename", gameName));
			nameValuePairs.add(new BasicNameValuePair("alias", codeName));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpclient.execute(httppost);

		} catch (IOException e) {

		}
	}

	public String getAlphaNumeric(int len) {
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			int ndx = (int) (Math.random() * ALPHA_NUM.length());
			sb.append(ALPHA_NUM.charAt(ndx));
		}
		return sb.toString();
	}

}