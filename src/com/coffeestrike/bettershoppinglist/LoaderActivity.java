package com.coffeestrike.bettershoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class LoaderActivity extends Activity {

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
		finish();
	}

	
	
}
