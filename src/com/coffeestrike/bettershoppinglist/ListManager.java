package com.coffeestrike.bettershoppinglist;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.util.Log;

/*
 * This class is a singleton.  Only one will ever be allowed
 * to exist at any given time.
 */
public class ListManager {
	
	private static final String TAG = "ListManager";

	//This file should contain the names of all the lists being managed.
	private static String FILENAME = "listmanager.json";
	
	private static String FILE_JSON_KEY = "listfilename";
	
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
	 * @return True if all lists were able to save successfully.
	 */
	public boolean save(){
		JSONArray jArray = new JSONArray();
		for(ShoppingList list : mAllLists){
			jArray.put(list.getListId().toString());
		}
		
		/*
		 * Now that the JSON object has been created, we need to write it to storage.
		 */
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(mAppContext.
					openFileOutput(FILENAME, Context.MODE_PRIVATE));
		} catch (FileNotFoundException e) {
			// Shouldn't happen because the file will be created, but WTH.
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Couldn't write list names to file", e);
		}
		
		
		return false;
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
