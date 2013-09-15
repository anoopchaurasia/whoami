package com.whoami;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyCallEndReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, MainService.class);
		i.putExtra("fromCallEnd", true);
		Log.d("Call Activi", "");
		context.startService(i);
	}
}
