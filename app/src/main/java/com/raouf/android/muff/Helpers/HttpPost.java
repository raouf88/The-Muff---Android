package com.raouf.android.muff.Helpers;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;


/**
 * helper class to handle the http posts.
 * to use construct an object passing the parameters as expected then call {@link #send()}
 *
 * @author Raouf
 */
class HttpPost extends AsyncTask<String, Integer, String > {

	/**
	 * holds post data pairs.
	 */
	private String postData;
	/**
	 * holds url to post to.
	 */
	private String url;
	/**
	 * holds {@link HttpPostListener} for callbacks
	 */
	private HttpPostListener listener;

	/**
	 * constructor with the data to httppost and response interface for callbacks.
	 *
	 * @param postData string of data to send. should be generated using {@link android.net.Uri.Builder} Eg:
	 *                 String postData = new Uri.Builder()
	 *					.appendQueryParameter(TAG, DATA_VALUE).build().getEncodedQuery();
	 * @param listener {@link HttpPostListener} for callbacks of the operation.
	 */
	HttpPost(@NonNull String url, @NonNull String postData, @NonNull HttpPostListener listener){
		this.url = url;
		this.postData=postData;
		this.listener=listener;		
	}

	/**
	 * called when {@link #executeOnExecutor(Executor, Object[])} is called, runs in the background thread.
	 * sends the {@link #postData} to {@link #url} using {@link HttpURLConnection}.
	 * if success returns the result as string , else returns null.
     */
	@Override
	protected String doInBackground(String... urls) {
		HttpURLConnection urlConnection = null;
		try {
			// create connection
			URL urlToRequest = new URL(url);
			urlConnection = (HttpURLConnection) urlToRequest.openConnection();
			// handle POST parameters
			if (postData != null) {
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setFixedLengthStreamingMode(
						postData.getBytes().length);
				urlConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				//send the POST out
				PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
				out.print(postData);
				out.close();
			}
			// if success
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return convertInputStreamToString(new BufferedInputStream(urlConnection.getInputStream()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return null;
	}

	/**
	 * called when the background process is finished. uses UI thread.
	 * trigger {@link #listener} onFinish then
	 * if operation was success it will call onSuccess with result msg
	 * if fails calls onFail.
	 */
	@Override
	protected void onPostExecute(String response) {
		listener.onFinish();
		// if the proccess is cancelld
		if (isCancelled()){
			response = null;
		}
		if (response == null){
			listener.onFail();
		} else {
			listener.onSuccess(response);
		}
	}

	/**
	 * executes http post.
	 */
	void send(){
		this.executeOnExecutor(THREAD_POOL_EXECUTOR);
	}


	/**
	 *
	 * @param inputStream InputStream to convert to String.
	 * @return String of InputStream
	 * @throws IOException if fails to read inputstram.
     */
	private String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line;
		String result = "";
		while((line = bufferedReader.readLine()) != null){
			result += line;
		}
		inputStream.close();
		return result;
	}

	/**
	 * interface to implement the callbacks of the http operation of {@link HttpPost}
	 */
	interface HttpPostListener
	{
		/**
		 * called when operation is finished.
		 */
		void onFinish();

		/**
		 * called when operation is success
		 * @param msg result from httppost
         */
		void onSuccess(String msg);

		/**
		 * called when operation is failed.
		 */
		void onFail();
	}

}
