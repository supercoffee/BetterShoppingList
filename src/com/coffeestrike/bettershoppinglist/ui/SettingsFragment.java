package com.coffeestrike.bettershoppinglist.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.coffeestrike.bettershoppinglist.R;

public class SettingsFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	}

}
