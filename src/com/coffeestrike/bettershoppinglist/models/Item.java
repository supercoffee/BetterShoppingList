package com.coffeestrike.bettershoppinglist.models;

import java.io.Serializable;
import java.util.Observable;
import java.util.UUID;

import android.util.Log;

public class Item extends Observable implements Serializable, Comparable<Item>{
	
	public interface OnStatusChangedListener{
		public void onStatusChanged(Item i);
	}
	
	private static final long serialVersionUID = -8439535938970924273L;
	private static final String TAG = "Item";
	/*
	 * Only status 0 and 1 are used at this time. 
	 * Essentially, this field is always converted to
	 * boolean values when used.
	 */
	private int mStatus; //0 = default, 1 = found, 2 = find later
	private String mDescription;
	private int mQuantity;

	protected UUID mId;
	private String mUnitOfMeasure;
	private OnStatusChangedListener mStatusListener;
	
	/*
	 * This may not be set until the item is retrieved from a server.
	 * In fact, this is completely normal.
	 * If the value hasn't been set, it will default to zero.
	 */
	private int mJSONId;

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
	
	public Item(){
		super();
		mDescription = "";
		mQuantity = 1;
		mUnitOfMeasure = sDefaultUomList[0];
		mId = UUID.randomUUID();
	}

	public Item(String description){
		super();
		mDescription = description;
		mQuantity = 1;
		mUnitOfMeasure = sDefaultUomList[0];
		mId = UUID.randomUUID();
	}
	
	public Item(String description, int quantity){
		super();
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
	

	public int getJSONId() {
		return mJSONId;
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

	public void setDescription(String description) {
		if (description != null && !description.equals(mDescription) ){
			mDescription = description;
			notifyObservers();

		}
	}

	public void setJSONId(int jSONId) {
		mJSONId = jSONId;
	}

	public void setQty(int qty) {
		if (qty != mQuantity) {
			mQuantity = qty;
			notifyObservers();
		}
	}

	public void setStatus(int status) {
		if(status != mStatus){
			mStatus = status;
			notifyObservers();
			mStatusListener.onStatusChanged(this);
		}
		
	}
	
	public void setStatusListener(OnStatusChangedListener statusListener) {
		this.mStatusListener = statusListener;
	}

	public void setUnitOfMeasure(String s) {
		if(! s.equals(mUnitOfMeasure)){
			mUnitOfMeasure = s;
			notifyObservers();
		}
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(mDescription);
		builder.append(" : ");
		builder.append(mQuantity);
		builder.append(" : ");
		builder.append(mUnitOfMeasure);
		builder.append(" : ");
		builder.append("JSONID=");
		builder.append(mJSONId);
		
		return builder.toString();
	}

	@Override
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	

}
