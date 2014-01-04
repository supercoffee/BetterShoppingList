package com.coffeestrike.bettershoppinglist.models;

import java.util.Observable;

import android.content.Context;

public class ItemSyncObserver extends ItemObserver {
	
	/*
	 * No need to waste resources by making each listener hold 
	 * a reference to the same SyncManager instance.
	 */
	private static SyncManager syncManagerInstance;

	public ItemSyncObserver(Context context) {
		super(context);
	}

	@Override
	public void update(Observable observable, Object obj) {
		
		if(syncManagerInstance == null){
			syncManagerInstance = SyncManager.getInstance(mAppContext);
		}
		
		if (syncManagerInstance.isSyncEnabled()) {
			Item item = (Item) observable;
			syncManagerInstance.updateItem(item);
		}

	}

}
