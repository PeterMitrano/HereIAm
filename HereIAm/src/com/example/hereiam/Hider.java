package com.example.hereiam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class Hider extends Activity implements JSONCallback, OnClickListener, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	TextView secrecyLevel;
	Button end;
	ScheduledFuture handle;
	LocationClient mLocationClient;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hider);

		secrecyLevel = (TextView) findViewById(R.id.hider_secrecy_level);
		end = (Button) findViewById(R.id.hider_end_game);
		end.setOnClickListener(this);
		mLocationClient = new LocationClient(this, this, this);
	}

	private void publishMyLocation() {
		Location loc = mLocationClient.getLastLocation();
		if (loc == null) {
			Log.e("loc null", "location is null");
			return;
		}
		String locStr = loc.getLatitude() + "," + loc.getLongitude();
		Log.e("loc", locStr);

		List<NameValuePair> scriptParams = new ArrayList<NameValuePair>();
		scriptParams.add(new BasicNameValuePair("hiderAlias", MainActivity.getAlias()));
		scriptParams.add(new BasicNameValuePair("hiderLocation", locStr));

		int period = 20; // get seeker locations every 10 seconds
		handle = new PostRequester("publish_hider_location", scriptParams, null).runPeriodically(period);
		// MAKE SURE THE CANCEL THIS HANDLE WHEN THE GAME ENDS
	}

	private void publishEndGame() {
		List<NameValuePair> scriptParams = new ArrayList<NameValuePair>();
		scriptParams.add(new BasicNameValuePair("hiderAlias", MainActivity.getAlias()));

		new GetRequester("end_game", scriptParams, this).runOnce();
		// MAKE SURE THE CANCEL THIS HANDLE WHEN THE GAME ENDS
	}

	@Override
	public void onJSONResponse(JSONArray result) {
		Log.e("response", "response get_hider_location " + result.toString());
		// update UI with secrecy level
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == end.getId()) {
			handle.cancel(true);
			publishEndGame();
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Really Exit?").setMessage("Selecting OK will eject you from the game!")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						handle.cancel(true);
						Hider.super.onBackPressed();
					}
				}).create().show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "error " + result.getErrorCode(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		handle.cancel(false);
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		if (handle == null) {
			publishMyLocation();
		}
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}
}