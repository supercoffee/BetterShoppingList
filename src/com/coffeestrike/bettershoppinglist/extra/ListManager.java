package com.coffeestrike.bettershoppinglist.extra;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.models.ShoppingList;
import com.coffeestrike.bettershoppinglist.ui.SettingsActivity;

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
	
	private String mServerURL;
	private String mRemoteListPath;
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(oos != null){
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	
	public ShoppingList getList(int position){
		if(mAllLists.size() == 0){
			newList();
		}
		return mAllLists.get(position);
	}
		
	
	/**
	 * Use this method to synchronize the list
	 * in JSON format to a remote server.
	 * 
	 */
	public void syncRemote(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
		 
		if(prefs.getBoolean(SettingsActivity.KEY_SYNC_REMOTE, false)){ //syncing is enabled
			mServerURL = prefs.getString(SettingsActivity.KEY_SERVER_URL_KEY,
					mAppContext.getResources().getString(R.string.default_server_url));
			mRemoteListPath = prefs.getString(SettingsActivity.KEY_SERVER_LIST_PATH, 
					mAppContext.getResources().getString(R.string.default_server_list_path));
		}
		
	}

}
