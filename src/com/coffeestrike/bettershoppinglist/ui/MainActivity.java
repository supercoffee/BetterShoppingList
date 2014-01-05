package com.coffeestrike.bettershoppinglist.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.models.Item;
import com.coffeestrike.bettershoppinglist.models.ListManager;


/**
 * 
 * @author Benjamin Daschel
 *
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends FragmentActivity{ 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.two_fragment_container);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

		ListManager.getInstance(this).load();
		
		/*
		 * Populate the list of available units of measure for the Item class
		 * It's done here because it requires inflation from a resource file,
		 * and passing this activity all the way to the Item class is bad OOP.
		 */
		int uomResId = R.array.units_of_measure_imperial;
		if(PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("pref_metric", false)){
			uomResId = R.array.units_of_measure_metric;
		}
		
		Item.sDefaultUomList = getResources().getStringArray(uomResId);

		FragmentManager fm = getSupportFragmentManager();
		
		Fragment lowerFragment = fm.findFragmentById(R.id.top_frame);
		if (lowerFragment == null) {
			fm.beginTransaction()
					.add(R.id.top_frame, new ShoppingListFragment())
					.commit();
		}
		
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		ListManager.getInstance(this).save();
	}
	
}
