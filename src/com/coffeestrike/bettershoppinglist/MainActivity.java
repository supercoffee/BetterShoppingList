package com.coffeestrike.bettershoppinglist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends FragmentActivity implements AddItemFragment.OnNewItemListener{
	
	protected static final int MESSAGE_SENT = 1;
	protected static final String MIME = "application/com.coffeestrike.bettershoppinglist";
	protected static final String TAG = "MainActivity";
	protected ShoppingList mShoppingList;
    
    


	public ShoppingList getShoppingList() {
		return mShoppingList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.two_fragment_container);
		
		mShoppingList = new ShoppingList(this);
		
		FragmentManager fm = getSupportFragmentManager();
		
		Fragment lowerFragment = fm.findFragmentById(R.id.bottom_frame);
		if (lowerFragment == null) {
			fm.beginTransaction()
					.add(R.id.bottom_frame, new ShoppingListFragment())
					.commit();
		}
		
		Fragment upperFragment = fm.findFragmentById(R.id.top_frame);
		if(upperFragment == null){
			fm.beginTransaction()
				.add(R.id.top_frame, new AddItemFragment())
				.commit();
		}
		
		
		

		
		Item.sDefaultUomList = getResources().getStringArray(R.array.units_of_measure);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}



	@Override
	public void onNewItem(Item i) {
		FragmentManager fm = getSupportFragmentManager();
		ShoppingListFragment shoppingListFragment = (ShoppingListFragment)fm.findFragmentById(R.id.bottom_frame);
		Intent intent = new Intent();
		intent.putExtra(Item.EXTRA_ITEM, i);
		shoppingListFragment.onActivityResult(ShoppingListFragment.NEW_ITEM, 
				Activity.RESULT_OK, intent);
	}

}
