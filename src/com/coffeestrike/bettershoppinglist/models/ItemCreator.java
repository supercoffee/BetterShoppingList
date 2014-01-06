package com.coffeestrike.bettershoppinglist.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * This class has the responsibility of creating Items 
 * in a ready to use state.  This class attaches observers
 * to items.
 * @author Benjamin Daschel
 *
 */
public class ItemCreator {
	private static final String JSON_CHECKED = "checked"; //has the item been checked off the list
	
	private static final String JSON_DESCRIPTION = "title"; //what are we shopping for
	/*
	 * This one is special.
	 * It's not a property that matters to the item,
	 * but rather to the server for update/delete purposes.
	 * If the item doesn't have this field initialized,
	 * then we can leave it blank and the server will
	 * create it upon POST.
	 */
	private static final String JSON_ID = "id";
	private static final String JSON_QUANTITY = "quantity"; // quantity of the item
	private static final String JSON_TIMESTAMP = "time"; //last modified time
	
	private static final String JSON_UOM = "units"; //unit of measure

	private static ItemCreator sInstance;
	
	private static final String TAG = "ItemCreator";
	
	public JSONObject createJSONObject(Item item) throws JSONException{
		JSONObject jsonObj = new JSONObject();

		jsonObj.accumulate(JSON_DESCRIPTION, item.getDescription().toString());
		jsonObj.accumulate(JSON_QUANTITY, item.getQty());
		jsonObj.accumulate(JSON_UOM, item.getUnitOfMeasure());
		jsonObj.accumulate(JSON_CHECKED, item.isChecked());
		jsonObj.accumulate(JSON_TIMESTAMP, item.getTimeStamp());

		/*
		 * We dont need to include the ID field in the JSON object because the server
		 * will handle the ID with a URL.
		 */
		
		Log.d(TAG, jsonObj.toString(4));
	
		return jsonObj;
	}
	public static ItemCreator getInstance(Context context){
		if(sInstance == null){
			sInstance = new ItemCreator(context);
			
		}
		return sInstance;
	}
	
	public Item createItem(JSONObject jsonObj) throws JSONException{
		Item item  = new Item();
		
		if (jsonObj.has(JSON_DESCRIPTION)) {
			item.setDescription(jsonObj.getString(JSON_DESCRIPTION));
		}
		
		if (jsonObj.has(JSON_QUANTITY)) {
			item.setQty(jsonObj.getInt(JSON_QUANTITY));
		}
		if (jsonObj.has(JSON_UOM)) {
			item.setUnitOfMeasure(jsonObj.getString(JSON_UOM));
		}
		if (jsonObj.has(JSON_CHECKED)) {
			item.setChecked(jsonObj.getBoolean(JSON_CHECKED));
		}	
		
		if (jsonObj.has(JSON_TIMESTAMP)){
			item.setTimeStamp(jsonObj.getLong(JSON_TIMESTAMP));
		}
		/*
		 * Needed when the object is modified so the server
		 * knows which record to update or delete.
		 */
		
		if (jsonObj.has(JSON_ID)) {
			item.setJSONId(jsonObj.getInt(JSON_ID));
		}
		
		item.addObserver(mItemSyncObserver);
		
		return item;
	}
	
	private Context mAppContext;
	
	private ItemObserver mItemSyncObserver;
	
	private ItemCreator(Context context){
		if (context != null) {
			mAppContext = context.getApplicationContext();
			mItemSyncObserver = new ItemSyncObserver(mAppContext);
		}
	}
	
	public Item createItem(){
		return this.createItem("", 1);
	}
	
	public Item createItem(String title){
		return this.createItem(title, 1);
	}
	
	public Item createItem(String title, int qty){
		Item item = new Item(title, qty);
		item.addObserver(mItemSyncObserver);
		return item;
	}

}
