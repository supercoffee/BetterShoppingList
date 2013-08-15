package com.example.bettershoppinglist;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class MainActivity extends FragmentActivity {
	
	private ArrayList<Item> mItemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container);
		
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().add(R.id.fragmentContainer,new ShoppingListFragment()).commit();

	}

	

}
