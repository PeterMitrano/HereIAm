package com.example.hereiam;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.util.Log;

// POST DATA--only write, no response required 
//-start game 
//-join game 
//-seeker exit
//-endgame
public class PostRequester {

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

	private PostRequest request;
	private String scriptName;
	private List<NameValuePair> scriptParams;
	private SuccessCallback callback;
	private PopupFragment requestFrag;
	private FragmentManager fm;
	private String dialogMessage;

	public PostRequester(String scriptName, List<NameValuePair> scriptParams, String dialogMessage, SuccessCallback callback,
			FragmentManager fm) {
		this.scriptParams = scriptParams;
		this.scriptName = scriptName;
		this.callback = callback;
		this.fm = fm;
		this.dialogMessage = dialogMessage;
	}

	public PostRequester(String scriptName, List<NameValuePair> scriptParams, SuccessCallback callback) {
		this.scriptParams = scriptParams;
		this.scriptName = scriptName;
		this.callback = callback;
	}

	public void runOnce() {
		if (dialogMessage == null || fm == null) {
			request = new PostRequest(scriptName, scriptParams, callback);
		} else {
			request = new PostRequest(scriptName, scriptParams, dialogMessage, callback, fm);
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

	private class PostRequest extends AsyncTask<String, Void, Boolean> {

		String scriptName;
		List<NameValuePair> scriptParams;
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
		public PostRequest(String scriptName, List<NameValuePair> scriptParams, String dialogMessage, SuccessCallback callback,
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
		public PostRequest(String scriptName, List<NameValuePair> scriptParams, SuccessCallback callback) {
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
			String url = buildURL();
			HttpPost httppost = new HttpPost(url);

			try {
				// Add your data
				httpclient.execute(httppost);
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
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