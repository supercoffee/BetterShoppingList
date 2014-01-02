package com.coffeestrike.bettershoppinglist.models;

import java.util.Observable;

import android.content.Context;
import android.os.Bundle;

public class ShoppingListSyncObserver extends ShoppingListObserver {
	
	/*
	 * No need to waste resources by making each listener hold 
	 * a reference to the same SyncManager instance.
	 */
	private static SyncManager syncManagerInstance;

	public ShoppingListSyncObserver(Context context) {
		super(context);
	}

	@Override
	public void update(Observable observable, Object data) {
		if(syncManagerInstance == null){
			syncManagerInstance = SyncManager.getInstance(mAppContext);
		}

		ShoppingList shoppingList = (ShoppingList) observable;
		
		/*
		 * Now we have to decide what happened to the shopping list
		 * so we know what to change server side.
		 */
		Bundle params = (Bundle) data;
		int operation = params.getInt(ShoppingList.EXTRA_OPERATION);
		switch(operation){
			case ShoppingList.ADD:
				break;
			case ShoppingList.DELETE:
				break;
			case ShoppingList.CLEAR:
				break;
			default:
				break;
		}
		
		
		
	}

}
