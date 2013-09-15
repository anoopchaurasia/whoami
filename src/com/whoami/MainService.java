package com.whoami;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;

public class MainService extends Service implements ResponseCollector {

	public static String token;

	@Override
	public void onCreate() {

		LocationListener location = new LocationListener(this);
		location.setService(this);
		token = readToken();
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						sendCurrentAppInfo();
						Thread.sleep(10000);
					}
				} catch (InterruptedException ex) {

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		Log.e("Destroy", "Destroy");
		super.onDestroy();
	}

	private String readToken() {

		String FILENAME = "token";
		try {
			BufferedReader inputReader = new BufferedReader(
					new InputStreamReader(openFileInput(FILENAME)));
			return inputReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	void updateWithNewLocation(Location location) throws ParseException {

		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair("latitude", "" + latitude));
			params.add(new BasicNameValuePair("longitude", "" + longitude));
			params.add(new BasicNameValuePair("timestamp", getCurrentTime()));
			sendToServer(params, "locationupdates");
		}
	}

	private String getCurrentPackage() {

		ActivityManager am = (ActivityManager) MainService.this
				.getSystemService(Activity.ACTIVITY_SERVICE);
		return am.getRunningTasks(1).get(0).topActivity.getPackageName();
	}

	String old_package = "";
	String startTime = "";

	private void sendCurrentAppInfo() throws ParseException {
		String current_package = getCurrentPackage();
		if (old_package.equals(current_package))
			return;
		if (startTime.equals("")) {
			startTime = getCurrentTime();
		}
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("activity", current_package));
		params.add(new BasicNameValuePair("end_time", getCurrentTime()));
		params.add(new BasicNameValuePair("start_time", startTime));
		sendToServer(params, "activities");
		startTime = getCurrentTime();
		old_package = current_package;
	}

	private void sendToServer(List<NameValuePair> params, String path) {
		new WebServices(path, MainService.this).execute(params);
	}

	@SuppressLint("SimpleDateFormat")
	private String getCurrentTime() throws ParseException {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				"yyyy-mm-dd HH:MM:ss");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		SimpleDateFormat dateFormatLocal = new SimpleDateFormat(
				"yyyy-mm-dd HH:MM:ss");
		Date d = dateFormatLocal.parse(dateFormatGmt.format(new Date()));
		return "" + d.getTime() / 1000;
	}

	@Override
	public void onResponse(String body, int code, String path) {
		Log.d("" + code, body);
	}

	private void sendCallActivity() {

		
		String[] strFields = { android.provider.CallLog.Calls.NUMBER,
				android.provider.CallLog.Calls.TYPE,
				android.provider.CallLog.Calls.CACHED_NAME,
				android.provider.CallLog.Calls.CACHED_NUMBER_TYPE };
		String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

		Cursor c = getContentResolver().query(
				android.provider.CallLog.Calls.CONTENT_URI, strFields, null,
				null, strOrder);
		String num = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));// for
		// number
		String name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for
		// name
		String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));// for
		// duration
		
		int type = Integer.parseInt(c.getString(c
				.getColumnIndex(CallLog.Calls.TYPE)));// for call type, Incoming
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("activity", num+"|"+name+"|"+duration+"|"+type));
		Log.d("calllog", num+"|"+name+"|"+duration+"|"+type);
		try {
			params.add(new BasicNameValuePair("end_time", getCurrentTime()));
			params.add(new BasicNameValuePair("start_time", getCurrentTime()));
			sendToServer(params, "activities");	
		} catch (ParseException e) {
			e.printStackTrace();
		}
												// or out going
		
		
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("intent",""+ intent.getBooleanExtra("fromCallEnd", false));
		if (intent.getBooleanExtra("fromCallEnd", false)) {
			sendCallActivity();
		}
		return START_STICKY;
	}
}
