package com.coffeestrike.bettershoppinglist.models;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;

/**
 * Watch for structural changes to the list.
 * If an item is deleted to added, the change must be synced to the server.
 * 
 * @author Benjamin Daschel
 *
 */
public abstract class ShoppingListObserver implements Observer {
	
	protected Context mAppContext;
	
	public ShoppingListObserver(Context context) {
		//Context required to interact with system resources
		mAppContext = context.getApplicationContext();
	}

	@Override
	public abstract void update(Observable observable, Object data);

}
