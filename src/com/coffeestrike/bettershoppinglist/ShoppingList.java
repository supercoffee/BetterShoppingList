package com.coffeestrike.bettershoppinglist;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class ShoppingList {
	
	private static final String TAG = "ShoppingList";
	private static final String FILENAME = "shoppinglist.json";
	private ShoppingListJSONSerializer mSerializer;
	
	private static ShoppingList sShoppingList;
	private ArrayList<Item> mItemList;
	private Context mAppContext;

	protected ShoppingList(Context appContext){
		mAppContext = appContext;
//		mItemList = new ArrayList<Item>();
		mSerializer = new ShoppingListJSONSerializer(FILENAME, mAppContext);
		try{
			mItemList = mSerializer.loadItems();
		}
		catch(Exception e){
			mItemList = new ArrayList<Item>();
			Log.e(TAG, "Error loading items from file", e);
		}
		
	}
	
	public static ShoppingList get(Context appContext){
		if (sShoppingList != null){
			return sShoppingList;
		}
		return new ShoppingList(appContext.getApplicationContext());
	}
	
	public ArrayList<Item> getList(){
		return mItemList;
	}
	
	public Item getItem(UUID id){
		for (Item i : mItemList){
			if (i.getId() == id){
				return i;
			}
		}
		return null;
	}
	
	public void add(Item i){
		mItemList.add(i);
	}
	
	public boolean saveList(){
		try{
			mSerializer.saveList(mItemList);
			Log.d(TAG, "list saved to file");
			return true;
		}
		catch(Exception e){
			Log.e(TAG, "Error saving list to file: ", e);
			return false;
		}
	}

}
