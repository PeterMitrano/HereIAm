package com.example.hereiam;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.util.Log;

// Makes GET Requests to server -- RETURNS data to user
// -getlocation
// -get game status

public class GetRequester {

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

	private ScheduledFuture handle;
	private GetRequest request;
	private String scriptName;
	private List<NameValuePair> scriptParams;
	private JSONCallback callback;
	private PopupFragment requestFrag;
	private FragmentManager fm;
	private String dialogMessage;

	public GetRequester(String scriptName, List<NameValuePair> scriptParams, String dialogMessage, JSONCallback callback,
			FragmentManager fm) {
		this.scriptParams = scriptParams;
		this.scriptName = scriptName;
		this.callback = callback;
		this.fm = fm;
		this.dialogMessage = dialogMessage;
	}

	public GetRequester(String scriptName, List<NameValuePair> scriptParams, JSONCallback callback) {
		this.scriptParams = scriptParams;
		this.scriptName = scriptName;
		this.callback = callback;
	}

	public void runOnce() {
		if (dialogMessage == null || fm == null) {
			request = new GetRequest(scriptName, scriptParams, callback);
		} else {
			request = new GetRequest(scriptName, scriptParams, dialogMessage, callback, fm);
		}
		request.execute();
	}

	public ScheduledFuture runPeriodically(int period) {
		Runnable taskable = new Runnable() {
			public void run() {
				try {
					runOnce();
				} catch (Exception e) {
					Log.e("scheduling error", e.getMessage());
				}
			}
		};

		return scheduler.scheduleWithFixedDelay(taskable, period, period, TimeUnit.SECONDS);
	}

	private class GetRequest extends AsyncTask<Object, Void, JSONArray> {

		private String scriptName;
		private List<NameValuePair> scriptParams;
		private JSONCallback callback;
		private PopupFragment requestFrag;
		private FragmentManager fm;

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
		public GetRequest(String scriptName, List<NameValuePair> scriptParams, String dialogMessage, JSONCallback callback,
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
		public GetRequest(String scriptName, List<NameValuePair> scriptParams, JSONCallback callback) {
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
		protected void onPostExecute(JSONArray result) {
			if (requestFrag != null) {
				requestFrag.dismiss();
			}
			if (callback != null) {
				if (result != null) {
					callback.onJSONResponse(result);
				} else {
					Log.e("post result", "null result on " + this.scriptName);
				}
			}
		}

		@Override
		protected JSONArray doInBackground(Object... params) {
			HttpClient httpclient = new DefaultHttpClient();
			String url = buildURL();
			HttpGet httpget = new HttpGet(url);

			try {
				// Add your data
				HttpResponse response = httpclient.execute(httpget);
				return getJSONFromResponse(response);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		private JSONArray getJSONFromResponse(HttpResponse response) throws Exception {
			HttpEntity entity = response.getEntity();
			String str = EntityUtils.toString(entity);
			Log.e("getJSONparse", str);
			JSONArray arr = null;
			try {
				arr = new JSONArray(str);

			} catch (Exception e) {
				Log.e("jsonparseerror", e.getMessage());
			}
			return arr;
		}

		private String buildURL() {
			String url = "http://sophocles.rmorey.me/hereiam/" + scriptName + "?";

			for (NameValuePair param : scriptParams) {
				url += param.getName() + "=" + param.getValue() + "&";

			}

			// Log.e("url", url);

			return url;
		}
	}
}
