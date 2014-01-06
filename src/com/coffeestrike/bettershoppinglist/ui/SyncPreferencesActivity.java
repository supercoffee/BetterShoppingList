package com.coffeestrike.bettershoppinglist.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.extra.OnDownloadFinishedListener;
import com.coffeestrike.bettershoppinglist.models.ListManager;
import com.coffeestrike.bettershoppinglist.models.ShoppingList;
import com.coffeestrike.bettershoppinglist.models.SyncManager;

public class SyncPreferencesActivity extends Activity{
	
	private static final String TAG = "SyncPreferencesActivity";
	private SyncManager mSyncManager;
	private String mServerURL;
	private String mRemoteListPath;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_prefs);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		
		mServerURL = prefs.getString(SettingsActivity.KEY_SERVER_URL_KEY,
				getApplicationContext().getResources().getString(R.string.default_server_url));
		mRemoteListPath = prefs.getString(SettingsActivity.KEY_SERVER_LIST_PATH, 
				getApplicationContext().getResources().getString(R.string.default_server_list_path));
		
		mSyncManager = SyncManager.getInstance(this);

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
	
	public void forceSync(View v){
		//TODO write the sync method
		
		mSyncManager.syncAll(null);
		Log.d(TAG, "User invoked force sync.");
	}
	
	
	/**
	 * Delete all the items from the server side
	 * list.
	 * @param v not used
	 */
	public void clearServerList(View v){
		mSyncManager.clearServerList();
		Log.d(TAG, "clearServerList");
	}
	

}
