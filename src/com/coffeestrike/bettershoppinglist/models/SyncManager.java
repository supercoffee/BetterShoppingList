package com.coffeestrike.bettershoppinglist.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.extra.JSONUtils;
import com.coffeestrike.bettershoppinglist.extra.OnDownloadFinishedListener;
import com.coffeestrike.bettershoppinglist.extra.SyncFinishedListener;
import com.coffeestrike.bettershoppinglist.ui.SettingsActivity;

/**
 * 
 * connection.setChunkedStreamingMode(0) has been commented out
 * in several places, because it seems to cause an EOFException.
 * 
 * @author Benjamin Daschel
 *
 */
public class SyncManager implements OnDownloadFinishedListener{

	private static SyncManager sSyncManager;
	
	public static SyncManager getInstance(Context context){
		if(sSyncManager== null){
			sSyncManager = new SyncManager(context);
		}
		return sSyncManager;
		
	}

	private Context mAppContext;
	private SharedPreferences mPreferences;
	private ItemCreator mItemCreator;
	private SyncFinishedListener mSyncFinishedListener;
	
	public static final String EXTRA_SHOPPING_LIST = "remote-list";
	
	private static final String TAG = "SyncManager";
	
	private SyncManager(Context context){
		mAppContext = context.getApplicationContext();
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
		mItemCreator = ItemCreator.getInstance(mAppContext);
	}
	
	public String getServerURL() {
		return mPreferences.getString(SettingsActivity.KEY_SERVER_URL_KEY,
				mAppContext.getResources().getString(R.string.default_server_url));
	}

	public String getRemoteListPath() {
		return mPreferences.getString(SettingsActivity.KEY_SERVER_LIST_PATH, 
				mAppContext.getResources().getString(R.string.default_server_list_path));
	}

	public void clearServerList(){
	
		new AsyncTask<Void, Void, Integer>(){

			@Override
			protected Integer doInBackground(Void... args) {
				
				//Array of JSON objects which represent Items, eventually
				JSONObject[] itemsFromServer = getList();
			
				if(itemsFromServer != null){
					for(JSONObject j : itemsFromServer){
						try{
							int itemId = j.getInt("id");
							deleteItem(itemId);
						}catch(JSONException e){
							Log.e(TAG, "JSON Object has not id field", e);
						}
						
					}

					return itemsFromServer.length;
				}
				else{
					Log.i(TAG, "No items found on server.");
					return 0;
				}
					
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				Log.i(TAG, String.format("Deleted %d items from remote.", result));
				
			}

		}.execute();

		
		Log.d(TAG, "clearServerList()");
		
	}
	
	public void createNewItem(Item item){
		
		new AsyncTask<Item, Void, Void>(){

			@Override
			protected Void doInBackground(Item... items) {
				try {
					JSONObject jsonItem = mItemCreator.createJSONObject(items[0]);
					String responseMessage = postItem(jsonItem);
					if(responseMessage.length() > 0){
						JSONObject response = new JSONObject(responseMessage);
						int newItemId = response.getInt("id");
						items[0].setJSONId(newItemId);
					}
					else{
						Log.d(TAG, "POST: response from server 0 length.");
					}
				} catch (JSONException e) {
					Log.e(TAG, "createNewItem", e);
				}
				return null;
			}
			
			
		}.execute(item);
		
	}
	
	/**
	 * Request that the the server delete the item
	 * specified by param j
	 * @param j Item to delete from server
	 */
	private void deleteItem(int itemId){
		
		HttpURLConnection connection = null;
		try {
			String itemPath = String.format(getServerURL() + getRemoteListPath().split("\\.")[0]
					+ "/%d.json", itemId);
			Log.d(TAG, "Requesting delete for URL\n" + itemPath);
			
			URL itemURL = new URL(itemPath);
			
			connection = (HttpURLConnection) itemURL.openConnection();
			connection.setRequestMethod("DELETE");
			
			connection.connect();
			
			Log.i(TAG, ""+connection.getResponseCode());
			
		} catch (MalformedURLException e) {
			Log.e(TAG, "URL derp!", e);
		} catch (IOException e) {
			Log.e(TAG, "Failed to DELETE for item:" + itemId, e);
		}
		finally{
			if(connection != null){
				connection.disconnect();
			}
		}
		
		
	}
	
	public void deleteItem(Item item){
		int itemId = item.getJSONId();
		/*
		 * We can't delete an item from the server without an id.
		 * Usually, this would happen because it hasn't been synced to
		 * the server yet.  
		 */
		if(itemId == 0){
			return;
		}
		
		
		new AsyncTask<Integer, Void, Void>(){

			@Override
			protected Void doInBackground(Integer... params) {
				deleteItem(params[0]);
				return null;
			}
			
			
		}.execute(itemId);
		
		
	}
	
	/**
	 * Uses HTTP GET command to retrieve the entire list.
	 * 
	 * @param serverURL
	 * @return
	 */
	public JSONObject[] getList(){
		JSONObject[] jsonItemsArray = null;
		try{
			URL url = new URL(getServerURL() + getRemoteListPath());
			String contents = getServerContents(url);

			JSONObject result = new JSONObject(contents);
			jsonItemsArray = JSONUtils.splitResults(result);
			
		} catch (JSONException e) {
			Log.e(TAG, "JSON exception", e);
		} catch (MalformedURLException e) {
			Log.e(TAG, "getList()", e);
		}
		
		return jsonItemsArray;
	}

	
	public void getRemoteList(final OnDownloadFinishedListener listener){
		
	
		new AsyncTask<Void, Void, Bundle>(){

			@Override
			protected Bundle doInBackground(Void... args) {
				JSONObject[] jsonItemsArray = getList();
				ShoppingList remoteList = new ShoppingList();
				if(jsonItemsArray != null){
					for(JSONObject obj: jsonItemsArray){
						try {
							Item item = mItemCreator.createItem(obj);
							remoteList.add(item);
						} catch (JSONException e) {
							Log.e(TAG, "Failed to create Item from JSON", e);
						}
					}
				}
				else{
					Log.d(TAG, "Server returned empty list.");
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable(EXTRA_SHOPPING_LIST, remoteList);
				return bundle;
				
			}
			
			@Override
			protected void onPostExecute(Bundle bundle){
				listener.onDownloadFinished(bundle);
			}
			
		}.execute();
		
	}

	
	/**
	 * Returns the results from a GET request as
	 * a String.
	 * @param url
	 * @return
	 */
	private String getServerContents(URL url){
		HttpURLConnection  httpConnection = null;
		InputStream iStream = null;
		StringBuilder contentBuilder = new StringBuilder();
		BufferedReader reader = null;
		try {
			httpConnection= (HttpURLConnection) url.openConnection();
			iStream = httpConnection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(iStream));
			String s;
			
			while( (s = reader.readLine()) != null){
				contentBuilder.append(s);
			}
			
			reader.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(httpConnection != null){
				httpConnection.disconnect();
			}
		}

		return contentBuilder.toString();
		
	}

	public boolean isSyncEnabled(){
		return mPreferences.getBoolean("pref_sync_remote", 
				false);
	}
	
	/**
	 * Uses HTTP POST to create a new item on the server.
	 * @param i
	 * @return
	 */
	private String postItem(JSONObject item){
		String response = "";
		String serverURLString = getServerURL() + getRemoteListPath();
		StringBuilder builder = new StringBuilder();
		HttpURLConnection connection = null;
		OutputStreamWriter writer = null;
		InputStream iStream = null;
		BufferedReader reader = null;
		int responseCode = -1;
		try{
			URL url = new URL(serverURLString);
			connection = (HttpURLConnection) url.openConnection();
		
			/*
			 * These parameters are set based on advice from 
			 * the official Android docs.
			 * http://developer.android.com/reference/java/net/HttpURLConnection.html
			 */
			connection.setDoOutput(true);
			connection.setDoInput(true);
//			connection.setChunkedStreamingMode(0);
		}catch(MalformedURLException e){
			Log.e(TAG, "URL derp!", e);
		} catch (IOException e) {
			Log.e(TAG, "Failed opening URL.", e);
		}
		
		/*
		 * Send the data to the server
		 */
		try {
			writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(item.toString());
			writer.close();
		} catch (IOException e) {
			Log.e(TAG, "Failed weriting output stream.", e);
		}

		/*
		 * Read the response from the server.
		 */
		try {
			iStream = connection.getInputStream();
			
			reader = new BufferedReader(new InputStreamReader(iStream));
			String s;
			while( (s = reader.readLine()) != null){
				builder.append(s);
			}
			reader.close();
			responseCode = connection.getResponseCode();
		} catch (IOException e) {
			Log.e(TAG, "Failed reading input stream.", e);
		}
		
		response = builder.toString();
		if(connection != null){
			Log.d(TAG, "POST results: " + responseCode);
			connection.disconnect();
		}

		
		return response;
	}
	
	/**
	 * Uses HTTP PUT to update an existing item on server.
	 * @param i
	 * @return
	 */
	private int putItem(JSONObject item, int id){
		int statusCode = 0;
		String serverURLString = String.format(getServerURL() + getRemoteListPath().split("\\.")[0]
				+ "/%d.json", id);
		
		try{
			URL url = new URL(serverURLString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
			/*
			 * These parameters are set based on advice from 
			 * the official Android docs.
			 * http://developer.android.com/reference/java/net/HttpURLConnection.html
			 */
			connection.setDoOutput(true);
			/*
			 * Causes intermittent IOExecptions when enabled.
			 * TODO investigate weird IOExceptions
			 */
//			connection.setChunkedStreamingMode(0);
			connection.setRequestMethod("PUT");

			
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(item.toString());
			writer.close();
			
			statusCode = connection.getResponseCode();
			connection.disconnect();
			
			Log.d(TAG, "PUT results: "+ statusCode);
		}
		catch(MalformedURLException e){
			Log.e(TAG, "postItem()", e);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return statusCode;
	}
	
	/**
	 * GET the entire list from the server.
	 * Merge the remote list with the local copy accounting 
	 * for items that exist in both.
	 * 
	 * Then update all the items from the local list to 
	 * the server. 
	 */
	public void syncAll(SyncFinishedListener listener) {
		if (isSyncEnabled()) {
			/*
			 * Start the list download process in the background.
			 */
			setSyncFinishedListener(listener);
			getRemoteList(this);
		}
		
	}

	public void updateItem(Item item){
		
		new AsyncTask<Item, Void, Integer>(){

			@Override
			protected Integer doInBackground(Item... items) {
				int statusCode = -1;
				try {
					JSONObject jsonItem = mItemCreator.createJSONObject(items[0]);
					statusCode = putItem(jsonItem, items[0].getJSONId());
					/*
					 * If we get a 404, the item hasn't been assigned an id 
					 * for some reason. Do a POST request instead.
					 */
					if(statusCode == HttpURLConnection.HTTP_NOT_FOUND){
						Log.d(TAG, "PUT results in 404. Trying POST instead.");
						String responseMessage = postItem(jsonItem);
						JSONObject response = new JSONObject(responseMessage);
						
						int newItemId = response.getInt("id");
						items[0].setJSONId(newItemId);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return statusCode;
			}
			
		}.execute(item);
	}

	@Override
	public void onDownloadFinished(Bundle bundle) {
		//This happens on the UI thread
		mergeLocalList(bundle);
		if(mSyncFinishedListener != null){
			mSyncFinishedListener.onSyncFinished();
		}
		
		/*
		 * Now, PUT or POST every item in the local 
		 * list to the server.
		 */
		ShoppingList localList = ListManager.getInstance(mAppContext).getLastUsedList();
		
		/*
		 * Now the local list has all the newest versions of the 
		 * item. Update all the items to ensure that  the server
		 * has the same information.
		 */
		for(Item localItem: localList){
			updateItem(localItem);
		}
		
		
		
	}

	
	private void mergeLocalList(Bundle bundle){
		ShoppingList localList = ListManager.getInstance(mAppContext).getLastUsedList();
		ShoppingList remoteList = (ShoppingList) bundle.getSerializable(EXTRA_SHOPPING_LIST);
		
		if(localList != null && remoteList != null){
			for(Item remoteItem : remoteList){
				/*
				 * If the item existed in the remote list,
				 * but no in the local list, add it to the local list.
				 */
				if(! localList.contains(remoteItem)){
					localList.add(remoteItem);
				}
				/*
				 * If the item existed in both lists and the 
				 * remote item is newer, replace it in the local list.
				 */
				else{
					int index = localList.indexOf(remoteItem);
					if(remoteItem.isNewer(localList.get(index))) {
						localList.replace(remoteItem, index);
					}
				}
				
				
			}
			
		}
	}

	public SyncFinishedListener getSyncFinishedListener() {
		return mSyncFinishedListener;
	}

	public void setSyncFinishedListener(SyncFinishedListener syncFinishedListener) {
		mSyncFinishedListener = syncFinishedListener;
	}



	
}
