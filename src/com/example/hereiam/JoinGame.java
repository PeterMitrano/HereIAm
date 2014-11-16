package com.example.hereiam;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class JoinGame extends Activity implements OnClickListener {

	Button join;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join_game);

		join = (Button) findViewById(R.id.join_game_join_button);

		join.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == join.getId()){
			//start join game dialog
		}
	}
}