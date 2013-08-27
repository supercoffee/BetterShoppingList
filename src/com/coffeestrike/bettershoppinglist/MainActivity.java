package com.coffeestrike.bettershoppinglist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.coffeestrike.bettershoppinglist.R;

public class MainActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.two_fragment_container);
		
		FragmentManager fm = getSupportFragmentManager();
		
		Fragment lowerFragment = fm.findFragmentById(R.id.bottom_frame);
		if (lowerFragment == null) {
			fm.beginTransaction()
					.add(R.id.bottom_frame, new ShoppingListFragment())
					.commit();
		}
		
//		Fragment upperFragment = fm.findFragmentById(R.id.top_frame);
//		if(upperFragment == null){
//			fm.beginTransaction()
//				.add(R.id.top_frame, new AddItemFragment())
//				.commit();
//		}

	}

	

}
