package com.coffeestrike.bettershoppinglist.extra;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.coffeestrike.bettershoppinglist.models.Item;

/**
 * Splits the resulting JSON object from HTTP GET
 * into individual JSON objects representing list items.
 * @author ben
 *
 */
public class JSONUtils {
	
	private static final String JSON_DESCRIPTION = "title"; //what are we shopping for
	
	private static final String JSON_QUANTITY = "quantity"; // quantity of the item
	private static final String JSON_UOM = "units"; //unit of measure
	private static final String JSON_CHECKED = "checked"; //has the item been checked off the list
	/*
	 * This one is special.
	 * It's not a property that matters to the item,
	 * but rather to the server for update/delete purposes.
	 * If the item doesn't have this field initialized,
	 * then we can leave it blank and the server will
	 * create it upon POST.
	 */
	private static final String JSON_ID = "id";
	private static final String TAG = "com.coffeestrike.betttershoppinglist.JSONItemParser";
	public static JSONObject createJSONObject(Item item) throws JSONException{
		JSONObject jsonObj = new JSONObject();

		jsonObj.accumulate(JSON_DESCRIPTION, item.getDescription().toString());
		jsonObj.accumulate(JSON_QUANTITY, item.getQty());
		jsonObj.accumulate(JSON_UOM, item.getUnitOfMeasure());
		jsonObj.accumulate(JSON_CHECKED, item.getStatus() == 1);

		/*
		 * We dont need to include the ID field in the JSON object because the server
		 * will handle the ID with a URL.
		 */
		
		Log.d(TAG, jsonObj.toString(4));
	
		return jsonObj;
	}
	
	public static Item readJSONObject(JSONObject jsonObj) throws JSONException{
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
			item.setStatus(jsonObj.getBoolean(JSON_CHECKED) ? 0 : 1);
			
		/*
		 * Needed when the object is modified so the server
		 * knows which record to update or delete.
		 */
		}
		if (jsonObj.has(JSON_ID)) {
			item.setJSONId(jsonObj.getInt(JSON_ID));
		}
		
		return item;
	}
	
	public static JSONObject[] splitResults(JSONObject results) throws JSONException{
		results = results.getJSONObject("results");
		JSONObject [] allItems = new JSONObject[results.length()];
		
		@SuppressWarnings("unchecked")
		Iterator<String> iter = results.keys();
		
		for(int i = 0; i < allItems.length; i++){
			allItems[i] = results.getJSONObject(iter.next());
		}
		
		return allItems;
	}


}
