package com.coffeestrike.bettershoppinglist;

import java.util.ArrayList;

import android.content.Context;

/*
 * This class is a singleton.  Only one will ever be allowed
 * to exist at any given time.
 */
public class ListManager {
	
	private static ListManager sListManager;
	private ArrayList<ShoppingList> mAllLists;
	private Context mAppContext;
	
	protected ListManager(Context context){
		mAppContext = context.getApplicationContext();
	}
	
	/**
	 * @param context Application {@link android.content.Context} required for instantiation.
	 * @return the sole instance of {@link ListManager} .
	 */
	public static ListManager getInstance(Context context){
		if (sListManager == null){
			sListManager = new ListManager(context);
		}
		return sListManager;
	}
	
	/**
	 * Loads the list from storage in preparation for usage.
	 */
	public void prepare(){
		//Load the lists now
	}
	
	/**
	 * @return {@link String} array containing the titles of lists currently loaded.
	 */
	public String[] getListTitles(){
		if(mAllLists == null){
			return null;
		}
		String [] titles = new String[mAllLists.size()];
		int i = 0;
		for(ShoppingList l : mAllLists){
			titles[i] = l.getListTitle();
			i++;
		}
		return titles;
	}

}
