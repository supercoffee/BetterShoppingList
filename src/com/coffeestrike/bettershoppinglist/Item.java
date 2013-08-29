package com.coffeestrike.bettershoppinglist;

import java.io.Serializable;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Item implements Serializable, Comparable<Item>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8439535938970924273L;
	private static final String TAG = "com.coffeestrike.bettershoppinglist.Item";
	private int mStatus; //0 = default, 1 = found, 2 = find later
	private String mDescription;
	private int mQty;
	private UUID mId;
	private String mUnitOfMeasure;
	private boolean mDislayQty;
	
	private static final String JSON_ID = "id";

	private static final String JSON_QTY = "qty";
	private static final String JSON_DESCRIPTION = "description";
	private static final String JSON_STATUS = "status";
	private static final String JSON_UOM = "uom";
	public Item(){
		mId = UUID.randomUUID();
	}
	
	
	public Item(JSONObject json) throws JSONException{
		mId = UUID.fromString(json.getString(JSON_ID));
		mDescription = json.getString(JSON_DESCRIPTION);
		mUnitOfMeasure = json.getString(JSON_UOM);
		mQty = json.getInt(JSON_QTY);
		mStatus = json.getInt(JSON_STATUS);
	}
	
	public Item(String description){
		mDescription = description;
		mId = UUID.randomUUID();
	}
	
	public boolean isDislayQty() {
		return mDislayQty;
	}


	public void setDislayQty(boolean dislayQty) {
		mDislayQty = dislayQty;
	}


	@Override
	public int compareTo(Item arg0) {
		return this.mDescription.toString().compareTo(arg0.mDescription.toString());
	}
	
	public CharSequence getDescription() {
		return mDescription;
	}
	public UUID getId() {
		return mId;
	}
	public int getQty() {
		return mQty;
	}
	public int getStatus() {
		return mStatus;
	}
	public String getUnitOfMeasure() {
		return mUnitOfMeasure;
	}

	public void setDescription(CharSequence description) {
		mDescription = description.toString();
	}

	public void setQty(int qty) {
		mQty = qty;
	}

	public void setStatus(int status) {
		Log.d(TAG, String.format("Status of item %s set to %d", getId().toString(), status));
		mStatus = status;
	}

	public void setUnitOfMeasure(String s) {
		mUnitOfMeasure = s;
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId);
		json.put(JSON_QTY, mQty);
		json.put(JSON_DESCRIPTION, mDescription);
		json.put(JSON_UOM, mUnitOfMeasure);
		json.put(JSON_STATUS, mStatus);
		return json;
	}


	@Override
	public boolean equals(Object o) {
		if(this.getClass().getSimpleName().equals(o.getClass().getSimpleName())){
			Item i = (Item) o;
			if(i.getId().equals(this.getId())){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	

}
