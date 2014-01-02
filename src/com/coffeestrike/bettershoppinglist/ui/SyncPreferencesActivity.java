package com.coffeestrike.bettershoppinglist.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.models.SyncManager;

public class SyncPreferencesActivity extends Activity {
	
	private static final String TAG = "SyncPreferencesActivity";
	private SyncManager mSyncManager;
	private String mServerURL;
	private String mRemoteListPath;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_prefs);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		
		mServerURL = prefs.getString(SettingsActivity.KEY_SERVER_URL_KEY,
				getApplicationContext().getResources().getString(R.string.default_server_url));
		mRemoteListPath = prefs.getString(SettingsActivity.KEY_SERVER_LIST_PATH, 
				getApplicationContext().getResources().getString(R.string.default_server_list_path));
		
		mSyncManager = SyncManager.getInstance(this);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home:
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				if(NavUtils.shouldUpRecreateTask(this, upIntent)){
					TaskStackBuilder.create(this)
					.addNextIntentWithParentStack(upIntent)
					.startActivities();
				}
				else{
					NavUtils.navigateUpTo(this, upIntent);
				}
				return true;
				
			default:
				return false;
		}
	}
	
	public void forceSyncUp(View v){
		Log.d(TAG, "forceSyncUp");
	}
	
	public void forceSyncDown(View v){
		Log.d(TAG, "forceSyncDown");
	}
	
	/**
	 * Delete all the items from the server side
	 * list.
	 * @param v not used
	 */
	public void clearServerList(View v){
		mSyncManager.clearServerList();
		Log.d(TAG, "clearServerList");
	}
		
//		try{
//			URL url = new URL(mServerURL);
//			
//			new AsyncTask<URL, Void, Integer>(){
//	
//				@Override
//				protected Integer doInBackground(URL... arg0) {
//					//Results is a single object representing the entire list
//					//we need to split it down to individual items
//					JSONObject results = getServerContents(arg0[0]);
//					
//					if(results == null){
//						return 0;
//					}
//					
//					//Array of JSON objects which represent Items, eventually
//					JSONObject[] itemsFromServer = null;
//					
//					try {
//						itemsFromServer = JSONResults.splitResults(results);
//						
//						if(itemsFromServer != null){
//							for(JSONObject j : itemsFromServer){
//								requestDelete(j);
//							}
//
//							return itemsFromServer.length;
//						}
//						else{
//							Log.i(TAG, "No items found on server.");
//							return 0;
//						}
//						
//					} catch (JSONException e) {
//						Log.e(TAG, "doInBackground", e);
//						return 0;
//					}
//				}
//
//				@Override
//				protected void onPostExecute(Integer result) {
//					super.onPostExecute(result);
//					Log.i(TAG, String.format("Deleted %d items from remote.", result));
//					
//				}
//
//			}.execute(url);
//		}
//		catch(MalformedURLException e){
//			Log.e(TAG, "Malformed URL", e);
//		}
//		
//		Log.d(TAG, "clearServerList");
//		
//	}
//	
//	public JSONObject getServerContents(URL serverURL){
//		HttpURLConnection  httpConnection = null;
//		InputStream iStream = null;
//		StringBuilder contentBuilder = new StringBuilder();
//		BufferedReader reader = null;
//		try {
//			httpConnection= (HttpURLConnection) new URL(serverURL, mRemoteListPath)
//			.openConnection();
//			iStream = httpConnection.getInputStream();
//			reader = new BufferedReader(new InputStreamReader(iStream));
//			String s;
//			
//			while( (s = reader.readLine()) != null){
//				contentBuilder.append(s);
//			}
//			
//			reader.close();
//
//			
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			if(httpConnection != null){
//				httpConnection.disconnect();
//			}
//		}
//		
//		try{
//			return new JSONObject(contentBuilder.toString());
//		} catch(JSONException e){
//			return null;
//		}
//	}
//
//	
//	/**
//	 * Request that the the server delete the item
//	 * specified by param j
//	 * @param j Item to delete from server
//	 */
//	public void requestDelete(JSONObject j){
//		
//		HttpURLConnection connection = null;
//		try {
//			int objectId = j.getInt("id");
//			String itemPath = String.format(mServerURL + mRemoteListPath.split("\\.")[0]
//					+ "/%d.json", objectId);
//			Log.d(TAG, "Requesting delete for URL\n" + itemPath);
//			
//			URL itemURL = new URL(itemPath);
//			
//			connection = (HttpURLConnection) itemURL.openConnection();
//			connection.setRequestMethod("DELETE");
//			
//			connection.connect();
//			
//			Log.i(TAG, ""+connection.getResponseCode());
//			
//			
//		} catch (JSONException e) {
//			Log.e(TAG, "Object j had no id field", e);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally{
//			if(connection != null){
//				connection.disconnect();
//			}
//		}
//		
//		
//	}
	

}
