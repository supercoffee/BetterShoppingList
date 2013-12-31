package com.coffeestrike.bettershoppinglist.extra;

import android.content.Context;


/**
 * Manages queries to local sqlite database.
 * @author ben
 *
 */
public class SQLiteListManager {

	/*
	 * Singleton instance of class
	 */
	private static SQLiteListManager sListManager;
	
	private Context mAppContext;
	private DBHelper mOpenerHelper;
	
	private SQLiteListManager(Context context){
		mAppContext = context.getApplicationContext();
	}
	
	public static SQLiteListManager getInstance(Context context){
		if(sListManager == null){
			sListManager = new SQLiteListManager(context);
		}
		return sListManager;
	}
	

	
	
}
