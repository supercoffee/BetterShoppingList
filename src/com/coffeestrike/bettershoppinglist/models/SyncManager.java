package com.coffeestrike.bettershoppinglist.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.coffeestrike.bettershoppinglist.extra.JSONResults;
import com.coffeestrike.bettershoppinglist.ui.SettingsActivity;

public class SyncManager {
	
	private static SyncManager sSyncManager;
	
	private Context mAppContext;

	private String mServerURL;

	private String mRemoteListPath;
	
	
	private static final String TAG = "SyncManager";
	
	
	private SyncManager(Context context){
		mAppContext = context.getApplicationContext();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
		
		mServerURL = prefs.getString(SettingsActivity.KEY_SERVER_URL_KEY,
				mAppContext.getResources().getString(R.string.default_server_url));
		mRemoteListPath = prefs.getString(SettingsActivity.KEY_SERVER_LIST_PATH, 
				mAppContext.getResources().getString(R.string.default_server_list_path));
	}
	
	public static SyncManager getInstance(Context context){
		if(sSyncManager== null){
			sSyncManager = new SyncManager(context);
		}
		return sSyncManager;
		
	}
	
	public void clearServerList(){
		
		try{
			URL url = new URL(mServerURL);
			
			new AsyncTask<URL, Void, Integer>(){


				@Override
				protected Integer doInBackground(URL... arg0) {
					//Results is a single object representing the entire list
					//we need to split it down to individual items
					JSONObject results = getServerContents();
					
					if(results == null){
						return 0;
					}
					
					//Array of JSON objects which represent Items, eventually
					JSONObject[] itemsFromServer = null;
					
					try {
						itemsFromServer = JSONResults.splitResults(results);
						
						if(itemsFromServer != null){
							for(JSONObject j : itemsFromServer){
								requestDelete(j);
							}

							return itemsFromServer.length;
						}
						else{
							Log.i(TAG, "No items found on server.");
							return 0;
						}
						
					} catch (JSONException e) {
						Log.e(TAG, "doInBackground", e);
						return 0;
					}
				}

				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					Log.i(TAG, String.format("Deleted %d items from remote.", result));
					
				}

			}.execute(url);
		}
		catch(MalformedURLException e){
			Log.e(TAG, "Malformed URL", e);
		}
		
		Log.d(TAG, "clearServerList");
		
	}
	
	public JSONObject getServerContents(){
		HttpURLConnection  httpConnection = null;
		InputStream iStream = null;
		StringBuilder contentBuilder = new StringBuilder();
		BufferedReader reader = null;
		try {
			httpConnection= (HttpURLConnection) new URL(new URL(mServerURL), mRemoteListPath)
			.openConnection();
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
		
		try{
			return new JSONObject(contentBuilder.toString());
		} catch(JSONException e){
			return null;
		}
	}

	
	/**
	 * Request that the the server delete the item
	 * specified by param j
	 * @param j Item to delete from server
	 */
	public void requestDelete(JSONObject j){
		
		HttpURLConnection connection = null;
		try {
			int objectId = j.getInt("id");
			String itemPath = String.format(mServerURL + mRemoteListPath.split("\\.")[0]
					+ "/%d.json", objectId);
			Log.d(TAG, "Requesting delete for URL\n" + itemPath);
			
			URL itemURL = new URL(itemPath);
			
			connection = (HttpURLConnection) itemURL.openConnection();
			connection.setRequestMethod("DELETE");
			
			connection.connect();
			
			Log.i(TAG, ""+connection.getResponseCode());
			
			
		} catch (JSONException e) {
			Log.e(TAG, "Object j had no id field", e);
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
	public JSONObject getItem(int itemId){
		//TODO stub
		return null;
	}
	
	/**
	 * Uses HTTP GET command to retrieve the entire list.
	 * 
	 * @param serverURL
	 * @return
	 */
	public JSONObject[] getList(){
		//TODO stub
		return null;
	}
	
	/**
	 * Uses HTTP POST to create a new item on the server.
	 * @param i
	 * @return
	 */
	public int postItem(Item i){
		int statusCode = 0;
		//TODO stub
		return statusCode;
	}
	
	/**
	 * Uses HTTP PUT to update an existing item on server.
	 * @param i
	 * @return
	 */
	public int putItem(Item i){
		int statusCode = 0;
		//TODO stub
		return statusCode;
	}

	
}
