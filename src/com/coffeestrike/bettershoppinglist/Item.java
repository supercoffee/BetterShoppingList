package com.coffeestrike.bettershoppinglist;

import java.io.Serializable;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Item implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8439535938970924273L;
	private static final String TAG = "com.coffeestrike.bettershoppinglist.Item";
	private int mStatus; //0 = default, 1 = found, 2 = find later
	private CharSequence mDescription;
	private int mQty;
	private UUID mId;
	
	private static final String JSON_ID = "id";
	private static final String JSON_QTY = "qty";
	private static final String JSON_DESCRIPTION = "description";
	private static final String JSON_STATUS = "status";
	
	
	public Item(){
		mId = UUID.randomUUID();
	}
	
	public Item(String description){
		mDescription = description;
		mId = UUID.randomUUID();
	}
	
	public UUID getId() {
		return mId;
	}
	public int getStatus() {
		return mStatus;
	}
	public void setStatus(int status) {
		Log.d(TAG, String.format("Status of item %s set to %d", getId().toString(), status));
		mStatus = status;
	}
	public CharSequence getDescription() {
		return mDescription;
	}
	public void setDescription(CharSequence description) {
		mDescription = description;
	}
	public int getQty() {
		return mQty;
	}
	public void setQty(int qty) {
		mQty = qty;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId);
		json.put(JSON_QTY, mQty);
		json.put(JSON_DESCRIPTION, mDescription);
		json.put(JSON_STATUS, mStatus);
		return json;
	}
	
	
	
	

}
