package com.coffeestrike.bettershoppinglist.extra;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Observer;

import android.content.Context;
import android.util.Log;

import com.coffeestrike.bettershoppinglist.models.ShoppingList;
import com.coffeestrike.bettershoppinglist.models.ShoppingListObserver;
import com.coffeestrike.bettershoppinglist.models.ShoppingListSyncObserver;

/*
 * This class is a singleton.  Only one will ever be allowed
 * to exist at any given time.
 */

public class ListManager {
	
	private static final String TAG = "ListManager";

	public static final String FILENAME = "listmanager";
	
	private static ListManager sListManager;
	private ArrayList<ShoppingList> mAllLists;
	private Context mAppContext;

	private ArrayList<Observer> mObservers;

	
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
			sListManager.mAllLists = new ArrayList<ShoppingList>();
		}
		sListManager.createObservers();
		return sListManager;
	}
	
	/**
	 * Loads the list from storage in preparation for usage.
	 */
	public void load(){
		ObjectInputStream ois = null;

		try{
			ois = new ObjectInputStream(mAppContext.openFileInput(FILENAME));
			mAllLists = (ArrayList<ShoppingList>) ois.readObject();
			
		} catch (StreamCorruptedException e) {
			Log.e(TAG, "Load failed", e);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Load failed", e);
		} catch (IOException e) {
			Log.e(TAG, "Load failed", e);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "Load failed", e);
		}
		finally{
			if(ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					Log.e(TAG, "Failed to close file.", e);
				}
			}
			
		}
	
	}
	

	public void save(){
		ObjectOutputStream oos = null;
		
		try{
			oos = new ObjectOutputStream(mAppContext.openFileOutput(FILENAME, Context.MODE_PRIVATE));
			oos.writeObject(mAllLists);
			Log.d(TAG, String.format("Wrote %d lists to storage.", mAllLists.size()));
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Save failed", e);
		} catch (IOException e) {
			Log.e(TAG, "Save failed", e);
		}
		finally{
			if(oos != null){
				try {
					oos.close();
				} catch (IOException e) {
					Log.e(TAG, "Failed to close file.", e);
				}
			}
		}


	}
	
	public String[] getAllUuidStrings(){
		if(mAllLists == null){
			return null;
		}
		String[] uuids = new String[mAllLists.size()];
		for(int i = 0; i < uuids.length; i++){
			uuids[i] = mAllLists.get(i).getListId().toString();
		}
		return uuids;
	}
	
	/**
	 * @return {@link String} array containing the titles of lists currently loaded.
	 */
	public String[] getAllTitles(){
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
	
	public void newList(){
		mAllLists.add(new ShoppingList());
	}
	
	/**
	 * Returns the requested ShoppingList.
	 * Attaches an observer to the list before returning.  
	 * @param position
	 * @return
	 */
	public ShoppingList getList(int position){
		if(mAllLists.size() == 0){
			newList();
		}
		ShoppingList s = mAllLists.get(position);
		attachObservers(s);
		return s;
	}
	
	
	/**
	 * Modify this method later to allow
	 * for additional observers.
	 */
	public void createObservers(){
		if(mObservers == null){
			mObservers = new ArrayList<Observer>();
		}
		mObservers.add(new ShoppingListSyncObserver(mAppContext));
	}
	
	public void attachObservers(ShoppingList list){
		for(Observer o : mObservers){
			list.addObserver(o);
		}
	}
	
	


}
