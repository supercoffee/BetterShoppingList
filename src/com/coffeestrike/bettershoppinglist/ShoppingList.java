package com.coffeestrike.bettershoppinglist;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

public class ShoppingList {
	
	private static ShoppingList sShoppingList;
	private ArrayList<Item> mItemList;
	@SuppressWarnings("unused")
	private Context mAppContext;

	protected ShoppingList(Context appContext){
		mAppContext = appContext;
		mItemList = new ArrayList<Item>();
		

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

}
