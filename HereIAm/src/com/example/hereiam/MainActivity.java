package com.example.hereiam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	Button newGame, joinGame;

	private static String codeName;

	public static String getCodeName() {
		return codeName;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		codeName = sharedPreferences.getString(getString(R.string.codename), getString(R.string.codename));

		newGame = (Button) this.findViewById(R.id.main_new_game_button);
		joinGame = (Button) this.findViewById(R.id.main_join_game_button);

		newGame.setOnClickListener(this);
		joinGame.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == newGame.getId()) {
			Intent newGameIntent = new Intent(this, StartGame.class);
			startActivity(newGameIntent);
		} else if (v.getId() == joinGame.getId()) {
			Intent newGameIntent = new Intent(this, JoinGame.class);
			startActivity(newGameIntent);
		}
	}
}