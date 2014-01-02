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
import android.preference.PreferenceManager;
import android.util.Log;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.extra.JSONUtils;
import com.coffeestrike.bettershoppinglist.ui.SettingsActivity;

public class SyncManager {
	
	private static SyncManager sSyncManager;
	
	public static SyncManager getInstance(Context context){
		if(sSyncManager== null){
			sSyncManager = new SyncManager(context);
		}
		return sSyncManager;
		
	}

	private Context mAppContext;
	private String mServerURL;
	private String mRemoteListPath;
	private SharedPreferences mPreferences;
	
	
	private static final String TAG = "SyncManager";
	
	private SyncManager(Context context){
		mAppContext = context.getApplicationContext();
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
		
		mServerURL = mPreferences.getString(SettingsActivity.KEY_SERVER_URL_KEY,
				mAppContext.getResources().getString(R.string.default_server_url));
		mRemoteListPath = mPreferences.getString(SettingsActivity.KEY_SERVER_LIST_PATH, 
				mAppContext.getResources().getString(R.string.default_server_list_path));
	}
	
	public boolean isSyncEnabled(){
		return mPreferences.getBoolean(mAppContext.getResources().getString(R.string.pref_sync_remote), 
				false);
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
		
		new AsyncTask<Item, Void, Integer>(){

			@Override
			protected Integer doInBackground(Item... items) {
				try {
					JSONObject jsonItem = JSONUtils.createJSONObject(items[0]);
					String responseMessage = postItem(jsonItem);
					JSONObject response = new JSONObject(responseMessage);
					
					int newItemId = response.getInt("id");
					items[0].setJSONId(newItemId);
					
					return newItemId;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return -1;
			}
			
			@Override
			protected void onPostExecute(Integer status){
				Log.d(TAG, "POST results: " + status);
			}
			
		}.execute(item);
		
	}
	
	public void updateItem(Item item){
		new AsyncTask<Item, Void, Integer>(){

			@Override
			protected Integer doInBackground(Item... items) {
				try {
					JSONObject JSONitem = JSONUtils.createJSONObject(items[0]);
					return putItem(JSONitem, items[0].getJSONId());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return -1;
			}
			
			@Override
			protected void onPostExecute(Integer status){
				Log.d(TAG, "PUT results: " + status);
			}
			
		}.execute(item);
	}
	
	public ShoppingList getShoppingList(){
		
		ShoppingList list = new ShoppingList();
	
		new AsyncTask<ShoppingList, Void, Void>(){

			@Override
			protected Void doInBackground(ShoppingList... shoppingLists) {
				ShoppingList shoppingList = shoppingLists[0];
				JSONObject[] theList = getList();
				
				for(JSONObject j : theList){
					try {
						shoppingList.add(JSONUtils.readJSONObject(j));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				return null;
			}
			
		}.execute();
		
		return list;
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
	 * Request that the the server delete the item
	 * specified by param j
	 * @param j Item to delete from server
	 */
	private void deleteItem(int itemId){
		
		HttpURLConnection connection = null;
		try {
			String itemPath = String.format(mServerURL + mRemoteListPath.split("\\.")[0]
					+ "/%d.json", itemId);
			Log.d(TAG, "Requesting delete for URL\n" + itemPath);
			
			URL itemURL = new URL(itemPath);
			
			connection = (HttpURLConnection) itemURL.openConnection();
			connection.setRequestMethod("DELETE");
			
			connection.connect();
			
			Log.i(TAG, ""+connection.getResponseCode());
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(connection != null){
				connection.disconnect();
			}
		}
		
		
	}

	
	/**
	 * Uses HTTP GET to retrieve an individual item.
	 * @param serverURL
	 * @param itemId
	 * @return
	 */
	private JSONObject getItem(int itemId){
		String itemURLString = String.format((mServerURL + mRemoteListPath.split("\\.")[0] 
				+ "/%d.json"), itemId);
		StringBuilder builder = new StringBuilder();
		try {
			
			URL url = new URL(itemURLString);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			InputStream iStream = connection.getInputStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
			String s;
			while( (s = reader.readLine()) != null){
				builder.append(s);
			}
			
			reader.close();
			connection.disconnect();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Uses HTTP GET command to retrieve the entire list.
	 * 
	 * @param serverURL
	 * @return
	 */
	private JSONObject[] getList(){
		try{
			URL url = new URL(mServerURL + mRemoteListPath);
			String contents = getServerContents(url);

			JSONObject result = new JSONObject(contents);
			JSONObject [] itemsList = JSONUtils.splitResults(result);
			return itemsList;
			
		} catch (JSONException e) {
			/*
			 * Hey, thats what the docs for this Exception 
			 * said to do. 
			 */
			throw new RuntimeException(e);
		} catch (MalformedURLException e) {
			Log.e(TAG, "getList()", e);
		}
		return null;
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
	
	/**
	 * Uses HTTP POST to create a new item on the server.
	 * @param i
	 * @return
	 */
	private String postItem(JSONObject item){
		String response = "";
		String serverURLString = mServerURL + mRemoteListPath;
		
		try{
			URL url = new URL(serverURLString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
			/*
			 * These parameters are set based on advice from 
			 * the official Android docs.
			 * http://developer.android.com/reference/java/net/HttpURLConnection.html
			 */
			connection.setDoOutput(true);
			connection.setChunkedStreamingMode(0);
			
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(item.toString());
			writer.close();
			
			response = connection.getResponseMessage();
			connection.disconnect();
		}
		catch(MalformedURLException e){
			Log.e(TAG, "postItem()", e);
		} catch (IOException e) {
			e.printStackTrace();
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
		String serverURLString = String.format(mServerURL + mRemoteListPath.split("\\.")[0]
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
			connection.setChunkedStreamingMode(0);
			connection.setRequestMethod("PUT");
			
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(item.toString());
			writer.close();
			
			statusCode = connection.getResponseCode();
			connection.disconnect();
		}
		catch(MalformedURLException e){
			Log.e(TAG, "postItem()", e);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return statusCode;
	}

	
}
