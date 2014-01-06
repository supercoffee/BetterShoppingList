package com.coffeestrike.bettershoppinglist.extra;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.coffeestrike.bettershoppinglist.models.Item;
import com.coffeestrike.bettershoppinglist.models.ShoppingList;

public class JSONUtils {
	

	private static final String TAG = "JSONItemParser";
	
	public static JSONObject[] splitResults(JSONObject results) throws JSONException{
		JSONObject [] allItems = null;
		
		if (! results.getString("results").equals("[]")) {
			results = results.getJSONObject("results");
			allItems = new JSONObject[results.length()];
			@SuppressWarnings("unchecked")
			Iterator<String> iter = results.keys();
			for (int i = 0; i < allItems.length; i++) {
				allItems[i] = results.getJSONObject(iter.next());
			}
		}
		return allItems;
	}

	public static ShoppingList shoppingListFromArray(JSONObject[] itemsList) {
		ShoppingList shoppingList = new ShoppingList();
		
//		if (itemsList != null) {
//			for (JSONObject j : itemsList) {
//				try {
//					Item item = null;//Item.readJSONObject(j);
//					shoppingList.add(item);
//				} catch (JSONException e) {
//					Log.e(TAG, "shoppingListFromArray", e);
//				}
//
//			}
//		}
		return shoppingList;
	}


}
