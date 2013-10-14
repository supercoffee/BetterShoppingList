package com.coffeestrike.bettershoppinglist;

import java.io.Serializable;
import java.util.UUID;

import android.graphics.Bitmap;
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
	protected UUID mId;
	private String mUnitOfMeasure;
	private OnStatusChangedListener mStatusListener;
	private Bitmap mItemImage; //for future use

	public Bitmap getItemImage() {
		return mItemImage;
	}

	public void setItemImage(Bitmap itemImage) {
		mItemImage = itemImage;
	}

	/*
	 * This array should be replaced with a list 
	 * defined in XML once the activity starts.
	 */
	public static String[] sDefaultUomList = {
		"Each",
		"Dozen",
		"Gallon",
	};

	public static final String EXTRA_ITEM = "Item";

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
		if (description != null) {
			mDescription = description.toString();
		}
	}

	public void setQty(int qty) {
		mQuantity = qty;
	}

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
