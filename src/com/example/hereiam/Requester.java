package com.example.hereiam;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.util.Log;

public class Requester extends AsyncTask<String, Void, Boolean> {

	String scriptName;
	List<NameValuePair> scriptParams;
	RequesterCallback callback;
	RequestFragment requestFrag;
	FragmentManager fm;

	public Requester(String scriptName, List<NameValuePair> sciptParams, String dialogMessage, RequesterCallback callback,
			FragmentManager fm) {
		this.scriptParams = sciptParams;
		this.scriptName = scriptName;
		this.callback = callback;

		requestFrag = new RequestFragment(dialogMessage);
	}

	@Override
	public void onPreExecute() {
		requestFrag.show(fm, scriptName + "_tag");
	}

	@Override
	protected void onPostExecute(Boolean result) {
		requestFrag.dismiss();
		callback.onRequesterResponse(result.booleanValue());
	}

	@Override
	protected Boolean doInBackground(String... params) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://sophocles.rmorey.me/hackrpi/" + scriptName + ".php");

		try {
			// Add your data

			httppost.setEntity(new UrlEncodedFormEntity(scriptParams));

			HttpResponse response = httpclient.execute(httppost);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			if (statusCode == 200) {
				return true;
			}
			Log.e("requester status code", statusCode + ", " + status.toString());
			return false;

		} catch (IOException e) {

		}
		return null;
	}

}
