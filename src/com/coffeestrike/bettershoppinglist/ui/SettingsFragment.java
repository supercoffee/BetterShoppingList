package com.coffeestrike.bettershoppinglist.ui;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.R.xml;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	}

}
