package com.whoami;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

class WebServices extends AsyncTask<List<? extends NameValuePair>, Void, Void> {
	
	ResponseCollector responseCollector;
	String URL = "http://ec2-54-254-105-248.ap-southeast-1.compute.amazonaws.com/";
	String path;
	String deviceId = "xxxxx";
	final String tag = "Your Logcat tag: ";

	protected Void doInBackground(List<? extends NameValuePair>... arg0) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(URL + path);
		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpContext localContext = new BasicHttpContext();
		List<BasicNameValuePair> params = (List<BasicNameValuePair>) arg0[0];
		//request.setHeader("Content-Type", "application/json");
		Log.d("url", URL + path);
		try {
			if(MainService.token != null && !MainService.token.equals("")){
				params.add( new BasicNameValuePair("token", MainService.token) );
			}

			params.add(new BasicNameValuePair("device","android"));
			request.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse result = httpclient.execute(request, localContext);
			int code = result.getStatusLine().getStatusCode();
			if(code != 200){
				BufferedReader reader = new BufferedReader(new InputStreamReader(result.getEntity().getContent(), "iso-8859-1"), 8);
	            StringBuilder sb = new StringBuilder();
	            sb.append(reader.readLine() + "\n");
	            String line = "0";
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	            reader.close();
	            String result11 = sb.toString();
	            Log.d("error", result11);
				responseCollector.onResponse("", code, path);

			}else{
				String body = handler.handleResponse(result);
				Log.d("param","" + body );
				Log.d("code","" + code );
				responseCollector.onResponse(body, code, path);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public WebServices(String url, ResponseCollector responseCollector) {
		this.path = url;
		this.responseCollector = responseCollector;
    }
}