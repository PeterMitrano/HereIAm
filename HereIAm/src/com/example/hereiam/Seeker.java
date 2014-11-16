package com.example.hereiam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Seeker extends Activity implements JSONCallback, GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	TextView location;
	ScheduledFuture getHiderHandle, pubLocHandle;
	GoogleMap map;
	LocationClient mLocationClient;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seeker);

		location = (TextView) findViewById(R.id.seeker_location);
		setUpMapIfNeeded();
		mLocationClient = new LocationClient(this, this, this);

		getHiderLocation();
	}

	private void getHiderLocation() {
		List<NameValuePair> scriptParams = new ArrayList<NameValuePair>();
		scriptParams.add(new BasicNameValuePair("seekerAlias", MainActivity.getAlias()));
		int period = 5; // get hider location every 60 seconds
		getHiderHandle = new GetRequester("get_hider_location", scriptParams, this).runPeriodically(period);
		// MAKE SURE THE CANCEL THIS HANDLE WHEN THE GAME ENDS
	}

	private void publishMyLocation() {
		Location loc = mLocationClient.getLastLocation();
		String locStr = loc.getLatitude() + "," + loc.getLongitude();
		Log.e("loc", locStr);
		List<NameValuePair> scriptParams = new ArrayList<NameValuePair>();
		scriptParams.add(new BasicNameValuePair("seekerAlias", MainActivity.getAlias()));
		scriptParams.add(new BasicNameValuePair("seekerLocation", locStr));

		int period = 10; // broadcast my seeker location every 10 seconds
		pubLocHandle = new PostRequester("publish_seeker_location", scriptParams, null).runPeriodically(period);
	}

	@Override
	public void onJSONResponse(JSONArray result) {
		Log.e("response", "response get_hider_location " + result.toString());
		// update UI with location
		location.setText(result.toString());
		map.addMarker(new MarkerOptions().position(new LatLng(40, -70)).title("seeker"));

	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Really Exit?").setMessage("Selecting OK will eject you from the game!")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						pubLocHandle.cancel(false);
						getHiderHandle.cancel(false);
						Seeker.super.onBackPressed();
					}
				}).create().show();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (map != null) {
				// The Map is verified. It is now safe to manipulate the map.

			}
		}

		map.setMyLocationEnabled(true);
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
		pubLocHandle.cancel(false);
		getHiderHandle.cancel(false);
		mLocationClient.disconnect();
		super.onStop();
	}

	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason.
			// resultCode holds the error code.
		} else {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getFragmentManager(), "Location Updates");
			}
		}
		return false;
	}

	public static class ErrorDialogFragment extends DialogFragment {
		private Dialog mDialog;

		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		if (pubLocHandle == null) {
			publishMyLocation();
		}

	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "error " + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
		}
	}

}