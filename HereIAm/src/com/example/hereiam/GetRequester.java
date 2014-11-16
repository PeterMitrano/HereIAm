package com.example.hereiam;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.FragmentManager;
import android.os.AsyncTask;

// Makes GET Requests to server -- RETURNS data to user
// -getlocation
// -get game status

public class GetRequester extends AsyncTask<String, Void, JSONObject> {

	String scriptName;
	List<NameValuePair> scriptParams;
	JSONCallback callback;
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
	public GetRequester(String scriptName, List<NameValuePair> scriptParams, String dialogMessage, JSONCallback callback,
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
	public GetRequester(String scriptName, List<NameValuePair> scriptParams, JSONCallback callback) {
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
	protected void onPostExecute(JSONObject result) {
		if (requestFrag != null) {
			requestFrag.dismiss();
		}
		if (callback != null) {
			callback.onJSONResponse(result);
		}
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://sophocles.rmorey.me/" + scriptName);

		try {
			// Add your data

			httppost.setEntity(new UrlEncodedFormEntity(scriptParams));
			HttpResponse response = httpclient.execute(httppost);
			return getJSONFromResponse(response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject getJSONFromResponse(HttpResponse response) throws Exception {
		HttpEntity entity = response.getEntity();
		InputStream inputStream = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		return new JSONObject(sb.toString());
	}
}
