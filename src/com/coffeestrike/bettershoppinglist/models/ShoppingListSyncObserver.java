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
		/*
		 * Check if sync is enabled. If not, do nothing and call it a day.
		 */
		if(syncManagerInstance.isSyncEnabled()){

			ShoppingList shoppingList = (ShoppingList) observable;
			
			/*
			 * Now we have to decide what happened to the shopping list
			 * so we know what to change server side.
			 */
			
			Bundle params = (Bundle) data;
			int operation = params.getInt(ShoppingList.EXTRA_OPERATION);
			Item item = (Item) params.getSerializable(ShoppingList.EXTRA_DATA);
			
			switch(operation){
				case ShoppingList.ADD:
					addItem(item);
					break;
				case ShoppingList.DELETE:
					removeItem(item);
					break;
				case ShoppingList.CLEAR:
					clearList(shoppingList);
					break;
				default:
					break;
			}
		}
		
	}

	private void clearList(ShoppingList shoppingList) {
		
		while(! shoppingList.isGarbageEmpty()){
			Item itemToRemove = shoppingList.pollGarbageQueue();
			syncManagerInstance.deleteItem(itemToRemove);
		}

		
	}

	private void removeItem(Item item) {
		syncManagerInstance.deleteItem(item);
		
	}

	private void addItem(Item item) {
		syncManagerInstance.createNewItem(item);
		
	}

}
