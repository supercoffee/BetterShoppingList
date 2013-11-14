package com.coffeestrike.bettershoppinglist.ui;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.R.string;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

public class SettingsActivity extends FragmentActivity {

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
