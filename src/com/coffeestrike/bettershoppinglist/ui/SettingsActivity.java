package com.coffeestrike.bettershoppinglist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

import com.coffeestrike.bettershoppinglist.R;

public class SettingsActivity extends FragmentActivity {
	
	public static final String KEY_SYNC_REMOTE = "prefs_sync_remote";
	public static final String KEY_SERVER_URL_KEY = "prefs_server_url";
	public static final String KEY_SERVER_LIST_PATH = "prefs_server_list_path";

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_settings);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home:
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				if(NavUtils.shouldUpRecreateTask(this, upIntent)){
					TaskStackBuilder.create(this)
					.addNextIntentWithParentStack(upIntent)
					.startActivities();
				}
				else{
					NavUtils.navigateUpTo(this, upIntent);
				}
				return true;
				
			default:
				return false;
		}
	}
	
	
	
	

}
