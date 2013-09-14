package com.whoami;

import java.io.FileOutputStream;
import java.io.IOException;

import com.whoami.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		Button btn = (Button)findViewById(R.id.logout);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 try {
					saveInFile();
					startMainActivity();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	private void saveInFile() throws IOException{
		String FILENAME = "token";
		String string = "";
		MainService.token = "";
		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		fos.write(string.getBytes());
		fos.close();
	}
	
	private void startMainActivity(){
		Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(nextScreen);
        this.finish();
	}
}
