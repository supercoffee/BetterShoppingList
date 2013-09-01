package com.coffeestrike.bettershoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class LoaderActivity extends Activity {

	private static final String TAG = "com.coffeestrike.bettershoppinglist.LoaderActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			Intent intent = new Intent(this, NfcMainActivity.class);
			startActivity(intent);
		}
		else{
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "Shutting down. I don't blame you.");
		finish();
	}

	
	
}
