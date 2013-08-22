package com.coffeestrike.bettershoppinglist;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

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
		Log.d(TAG, String.format("Wrote %d items to storage", shoppingList.size()));
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
	
	public ArrayList<Item> loadItems() throws IOException, JSONException{
		ArrayList<Item> items = new ArrayList<Item>();
		
		BufferedReader reader = null;
		try{
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				jsonString.append(line);
			}
			// parse the JSON array using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			for(int i = 0; i < array.length(); i++){
				items.add(new Item(array.getJSONObject(i)));
			}
		}//end try
		catch(FileNotFoundException f){
			// nothing to do here
		}
		finally{
			if(reader != null){
				reader.close();
			}
		}
		
		return items;
	}
	

}
