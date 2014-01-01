package com.coffeestrike.bettershoppinglist.models;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;

/**
 * Watch for structural changes to the list.
 * If an item is deleted to added, the change must be synced to the server.
 * 
 * @author ben
 *
 */
public class ShoppingListObserver implements Observer {
	
	private Context mAppContext;
	
	public ShoppingListObserver(Context context) {
		mAppContext = context.getApplicationContext();
	}

	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub

	}

}
