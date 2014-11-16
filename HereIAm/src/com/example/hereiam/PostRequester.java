package com.example.hereiam;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.util.Log;

// POST DATA--only write, no response required 
//-start game 
//-join game 
//-seeker exit
//-endgame
public class PostRequester extends AsyncTask<String, Void, Boolean> {

	String scriptName;
	HttpParams scriptParams;
	SuccessCallback callback;
	PopupFragment requestFrag;
	FragmentManager fm;

	/**
	 * 
	 * @param scriptName
	 *            name of the script
	 * @param sciptParams
	 *            list of parameters
	 * @param dialogMessage
	 *            string to display on popup
	 * @param callback
	 *            class implementing callback
	 * @param fm
	 *            fragment manger from the class showing the popup
	 */
	public PostRequester(String scriptName, HttpParams scriptParams, String dialogMessage, SuccessCallback callback,
			FragmentManager fm) {
		this.scriptParams = scriptParams;
		this.scriptName = scriptName;
		this.callback = callback;
		this.fm = fm;
		requestFrag = new PopupFragment(dialogMessage);
	}

	/**
	 * 
	 * @param scriptName
	 *            name of the script
	 * @param sciptParams
	 *            list of parameters
	 * @param callback
	 *            class implementing callback
	 */
	public PostRequester(String scriptName, HttpParams scriptParams, SuccessCallback callback) {
		this.scriptParams = scriptParams;
		this.scriptName = scriptName;
		this.callback = callback;
	}

	@Override
	public void onPreExecute() {
		if (requestFrag != null) {
			requestFrag.show(fm, scriptName + "_tag");
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (requestFrag != null) {
			requestFrag.dismiss();
		}
		if (callback != null) {
			callback.onRequesterResponse(result.booleanValue());
		}
	}

	@Override
	protected Boolean doInBackground(String... params) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://sophocles.rmorey.me/" + scriptName);

		try {
			// Add your data
			httppost.setParams(scriptParams);

			HttpResponse response = httpclient.execute(httppost);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			if (statusCode == 200) {
				return true;
			}
			Log.e("requester status code", statusCode + "");
			return false;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
