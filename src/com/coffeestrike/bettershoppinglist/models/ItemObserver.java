package com.coffeestrike.bettershoppinglist.models;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;

/**
 * When an Item is changed by the user,
 * we need to sync it against to the server if required.
 * @author Benjamin Daschel
 *
 */
public abstract class ItemObserver implements Observer {
	
	protected Context mAppContext;
	
	public ItemObserver(Context context) {
		//Context required to interact with system resources
		mAppContext = context.getApplicationContext();
	}

	@Override
	public abstract void update(Observable item, Object obj);
}
