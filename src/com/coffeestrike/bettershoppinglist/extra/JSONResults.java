package com.coffeestrike.bettershoppinglist.extra;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Splits the resulting JSON object from HTTP GET
 * into individual JSON objects representing list items.
 * @author ben
 *
 */
public class JSONResults {
	
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
