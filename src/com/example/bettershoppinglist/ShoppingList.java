package com.example.bettershoppinglist;

import java.util.ArrayList;

import android.content.Context;

public class ShoppingList {
	
	private static ShoppingList sShoppingList;
	private ArrayList<Item> mItemList;
	private Context mAppContext;

	protected ShoppingList(){
		mItemList = new ArrayList<Item>();
	}
	
	public ShoppingList getList(Context appContext){
		if (sShoppingList != null){
			return sShoppingList;
		}
		return new ShoppingList();
	}
	

}
