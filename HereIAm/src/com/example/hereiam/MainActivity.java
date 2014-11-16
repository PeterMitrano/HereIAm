package com.example.hereiam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	Button newGame, joinGame;

	private static String alias;

	public static String getAlias() {
		return alias;
	}

	public static String setAlias(String a) {
		return alias = a;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		alias = sharedPreferences.getString(getString(R.string.alias), null);

		if (alias == null) {
			Log.e("no alias preference", "no alias");
			new EditAliasFragment(sharedPreferences.edit()).show(getFragmentManager(), "set_alias_tag");
		}

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