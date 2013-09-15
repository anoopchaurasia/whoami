package com.whoami;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.whoami.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements ResponseCollector {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String token = readToken();
		setContentView(R.layout.activity_main);
		Intent serviceIntent = new Intent(this, MainService.class);
		startService(serviceIntent);
		if (token == null || token.equals("")) {
			Button btn = (Button) findViewById(R.id.saveButton);
			final EditText txt = (EditText) findViewById(R.id.email);
			final EditText txt2 = (EditText) findViewById(R.id.token);
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					handleClick(txt.getText().toString(), txt2.getText()
							.toString());
				}
			});
		} else {
			changeActivity();
		}
	}

	private void handleClick(String email, String token) {
		if (!token.trim().equals("") && !email.trim().equals("")) {
			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair("token", token));
			params.add(new BasicNameValuePair("email", email));
			new WebServices("users/authenticates", MainActivity.this)
					.execute(params);
			findViewById(R.id.error).setVisibility(View.INVISIBLE);
		}
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

	final static String USER_INFO = "user_basic_info";

	@Override
	public void onResponse(String response, int code, String path) {
		Log.d("" + code, response);
		try {
			if (code == 200 && !response.equals("false")) {
				JSONObject jb = new JSONObject(response);
				saveInFile((String) jb.get("token"));
				changeActivity();
			} else {
				h.sendEmptyMessage(0);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveInFile(String token) throws IOException {
		String FILENAME = "token";
		String string = token;
		MainService.token = token;
		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		fos.write(string.getBytes());
		fos.close();
	}

	Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				MainActivity.this.findViewById(R.id.error).setVisibility(View.VISIBLE);
				break;
			}
			}
		}
	};

	private void changeActivity() {
		Intent nextScreen = new Intent(getApplicationContext(),
				WelcomeActivity.class);
		startActivity(nextScreen);
		this.finish();
	}
}