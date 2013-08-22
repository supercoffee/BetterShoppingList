package com.coffeestrike.bettershoppinglist;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

public class ShoppingListJSONSerializer {
	
	private static final String TAG = "ShoppingListJSONSerializer";
	private String mFilename;
	private Context mContext;
	
	public ShoppingListJSONSerializer(String mFilename, Context mContext) {
		this.mFilename = mFilename;
		this.mContext = mContext;
	}
	
	public void saveList(ArrayList<Item> shoppingList) throws JSONException, IOException{
		
		//Build new JSON array
		JSONArray jArray = new JSONArray();
		for (Item i: shoppingList){
			jArray.put(i.toJSON());
		}
		Log.d(TAG, String.format("Wrote %d items to storgae", shoppingList.size()));
		Writer writer = null;
		try{
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(jArray.toString());
		}
		finally{
			if(writer != null)
				writer.close();
		}
		
	}
	
	

}
