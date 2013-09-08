package com.coffeestrike.bettershoppinglist;

import java.io.Serializable;
import java.util.UUID;

import android.util.Log;

public class Item implements Serializable, Comparable<Item>{
	
	public interface OnStatusChangedListener{
		public void onStatusChanged(Item i);
	}
	
	private static final long serialVersionUID = -8439535938970924273L;
	private static final String TAG = "com.coffeestrike.bettershoppinglist.Item";
	private int mStatus; //0 = default, 1 = found, 2 = find later
	private String mDescription;
	private int mQuantity;
	private UUID mId;
	private String mUnitOfMeasure;
	private transient OnStatusChangedListener mStatusListener;

	/*
	 * This array should be replaced with a list 
	 * defined in XML once the activity starts.
	 */
	public static String[] sDefaultUomList = {
		"Each",
		"Dozen",
		"Gallon",
	};

	//	private static final String JSON_ID = "id";
//
//	private static final String JSON_QUANTITY = "quantity";
//	private static final String JSON_DESCRIPTION = "description";
//	private static final String JSON_STATUS = "status";
//	private static final String JSON_UOM = "uom";
	public static final String EXTRA_ITEM = "com.coffeestrike.bettershoppinglist.Item";
//
//	
//	
//	public Item(JSONObject json) throws JSONException{
//		mId = UUID.fromString(json.getString(JSON_ID));
//		mDescription = json.getString(JSON_DESCRIPTION);
//		mUnitOfMeasure = json.getString(JSON_UOM);
//		mQuantity = json.getInt(JSON_QUANTITY);
//		mStatus = json.getInt(JSON_STATUS);
//	}

	public Item(String description){
		mDescription = description;
		mQuantity = 1;
		mUnitOfMeasure = sDefaultUomList[0];
		mId = UUID.randomUUID();
	}
	
public Item(String description, int quantity){
		mDescription = description;
		mQuantity = quantity;
		mUnitOfMeasure = sDefaultUomList[0];
		mId = UUID.randomUUID();
	}
	
	@Override
	public int compareTo(Item arg0) {
		return this.mDescription.toString().compareTo(arg0.mDescription.toString());
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
	

	public CharSequence getDescription() {
		return mDescription;
	}
	
	public UUID getId() {
		return mId;
	}
	public int getQty() {
		return mQuantity;
	}
	public int getStatus() {
		return mStatus;
	}
	public OnStatusChangedListener getStatusListener() {
		return mStatusListener;
	}
	public String getUnitOfMeasure() {
		return mUnitOfMeasure;
	}

	public boolean isDivider() {
		return false;
	}

	public boolean isEmpty(){
		if(mDescription.equals("")){
			return true;
		}
		return false;
	}

	public void setDescription(CharSequence description) {
		mDescription = description.toString();
	}

	public void setQty(int qty) {
		mQuantity = qty;
	}

//	public JSONObject toJSON() throws JSONException {
//		JSONObject json = new JSONObject();
//		json.put(JSON_ID, mId);
//		json.put(JSON_QUANTITY, mQuantity);
//		json.put(JSON_DESCRIPTION, mDescription);
//		if (mUnitOfMeasure == null) {
//			mUnitOfMeasure = sDefaultUomList[0];
//		}
//		json.put(JSON_UOM, mUnitOfMeasure);
//		json.put(JSON_STATUS, mStatus);
//		return json;
//	}


	public void setStatus(int status) {
		mStatus = status;
		mStatusListener.onStatusChanged(this);
		Log.d(TAG, String.format("Status of item %s set to %d", getId().toString(), status));
		
	}
	
	
	public void setStatusListener(OnStatusChangedListener statusListener) {
		this.mStatusListener = statusListener;
	}

	public void setUnitOfMeasure(String s) {
		mUnitOfMeasure = s;
	}


}
