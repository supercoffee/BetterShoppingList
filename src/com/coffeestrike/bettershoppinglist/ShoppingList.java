package com.coffeestrike.bettershoppinglist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

/**
 * @author Benjamin Daschel
 * Extension of ArrayList with ability to save and load from file
 *
 */
public class ShoppingList extends ArrayList<Item>{
	
	private static final String TAG = "ShoppingList";
	private static final String FILENAME = "shoppinglist.json";
	private transient ShoppingListJSONSerializer mSerializer;
	private transient Context mAppContext;
//	private static ShoppingList sShoppingList;
	

	public ShoppingList(Context appContext){
		mAppContext = appContext;
		mSerializer = new ShoppingListJSONSerializer(FILENAME, mAppContext);
	}
	
//	public static ShoppingList get(Context appContext){
//		if (sShoppingList != null){
//			return sShoppingList;
//		}
//		ShoppingList s = new ShoppingList(appContext.getApplicationContext());
//		s.loadList();
//		return s;
//	}
	
	
	public boolean loadList(){
		try {
			mSerializer.loadItems(ShoppingList.this);
		} catch (JSONException e) {
			Log.e(TAG, "Unable to load from JSON", e);
			
		} catch (IOException e) {
			Log.e(TAG, "Unable to load list from storage", e);
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean saveList(){
		try{
			mSerializer.saveList(ShoppingList.this);
			Log.d(TAG, "list saved to file");
			return true;
		}
		catch(Exception e){
			Log.e(TAG, "Error saving list to file: ", e);
			return false;
		}
	}

	public void sortAlpha() {
		Collections.sort(ShoppingList.this);
	}

	public void merge(ShoppingList incomingList) {
//		if(sShoppingList == null){
//			sShoppingList = new ShoppingList(activity.getApplicationContext());
//		}
		for(Item item : incomingList){
			if(! this.contains(item)){
				add(item);
			}
		}
		
	}


}
