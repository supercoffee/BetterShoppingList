package com.coffeestrike.bettershoppinglist.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Observable;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Item extends Observable implements Serializable, Comparable<Item>{
	
	public interface OnStatusChangedListener{
		public void onStatusChanged(Item i);
	}
	
	private static final long serialVersionUID = -8439535938970924273L;
	
	private static final String JSON_DESCRIPTION = "title"; //what are we shopping for
	
	private static final String JSON_QUANTITY = "quantity"; // quantity of the item
	private static final String JSON_UOM = "units"; //unit of measure
	private static final String JSON_CHECKED = "checked"; //has the item been checked off the list
	private static final String JSON_TIMESTAMP = "time"; //last time updated/created

	/*
	 * Only status 0 and 1 are used at this time. 
	 * Essentially, this field is always converted to
	 * boolean values when used.
	 */
	
	@Deprecated
	private int mStatus; //0 = not checked, 1 = checked
	
	private boolean mChecked;
	private String mDescription;
	private int mQuantity;
	private long mTimeStamp;
	private String mUnitOfMeasure;
	@Deprecated
	protected UUID mId;
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
	
	public static JSONObject toJSON(Item item) throws JSONException{
		JSONObject result = new JSONObject();
	
		result.put(JSON_DESCRIPTION, item.mDescription)
			.put(JSON_QUANTITY, item.mQuantity)
			.put(JSON_UOM, item.mUnitOfMeasure)
			.put(JSON_CHECKED, item.mChecked)
			.put(JSON_TIMESTAMP, item.mTimeStamp);

		return result;
		
	}
	
	public static Item fromJSON(JSONObject jObject) throws JSONException{
		Item result = new Item();

		if(jObject.has(JSON_DESCRIPTION)){
			result.mDescription = jObject.getString(JSON_DESCRIPTION);
		}
		if(jObject.has(JSON_QUANTITY)){
			result.mQuantity =jObject.getInt(JSON_QUANTITY) ;
		}
		if(jObject.has(JSON_UOM)){
			result.mUnitOfMeasure = jObject.getString(JSON_UOM);
		}
		if(jObject.has(JSON_CHECKED)){
			result.mChecked = jObject.getBoolean(JSON_CHECKED);
		}
		if(jObject.has(JSON_TIMESTAMP)){
			result.mTimeStamp = jObject.getLong(JSON_TIMESTAMP);
		}
		
		return result;
	}
	
	public Item(){
		this("", 1);
	}

	public Item(String description){
		this(description, 1);
	}
	
	public Item(String description, int quantity){
		mDescription = description;
		mQuantity = quantity;
		mUnitOfMeasure = UnitsOfMeasure.getUnits()[0];
		mId = UUID.randomUUID();
		setTimeStamp();
	}
	

	@Override
	public int compareTo(Item arg0) {
		return this.mDescription.toString().compareTo(arg0.mDescription.toString());
	}

	@Override
	public boolean equals(Object o) {
		if(this.getClass().getSimpleName().equals(o.getClass().getSimpleName())){
			Item i = (Item) o;
			/*
			 * If either items have unset JSON IDs, then compare the 
			 * descriptions.
			 */
			if(i.mJSONId == 0 || mJSONId == 0){
				return this.mDescription.equals(i.mDescription);
			}
			
			else{
				return this.mJSONId == i.mJSONId;
			}
			
		}
		return false;
	}

	public CharSequence getDescription() {
		return mDescription;
	}
	
	@Deprecated
	public UUID getId() {
		return mId;
	}
	

	public int getJSONId() {
		return mJSONId;
	}
	
	public int getQty() {
		return mQuantity;
	}
	
	@Deprecated
	public int getStatus() {
		return mStatus;
	}
	public OnStatusChangedListener getStatusListener() {
		return mStatusListener;
	}
	/**
	 * Give the time when this Item was last modified, or created.  
	 * @return the time in milliseconds when Item was last modified.
	 */
	public long getTimeStamp() {
		return mTimeStamp;
	}
	public String getUnitOfMeasure() {
		return mUnitOfMeasure;
	}

	public boolean isChecked() {
		return mChecked;
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

	@Override
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	public void setChecked(boolean checked) {
		mChecked = checked;
		setTimeStamp();
		notifyObservers();
		if (mStatusListener != null) {
			mStatusListener.onStatusChanged(this);
		}
	}
	
	public void setDescription(String description) {
		if (description != null && !description.equals(mDescription) ){
			mDescription = description;
			setTimeStamp();
			notifyObservers();

		}
	}

	public void setJSONId(int jSONId) {
		mJSONId = jSONId;
	}
	
	public void setQty(int qty) {
		if (qty != mQuantity) {
			mQuantity = qty;
			setTimeStamp();
			notifyObservers();
		}
	}

	@Deprecated
	public void setStatus(int status) {
		if(status != mStatus){
			mStatus = status;
			notifyObservers();
			if (mStatusListener != null) {
				mStatusListener.onStatusChanged(this);
			}
		}
		
	}

	public void setStatusListener(OnStatusChangedListener statusListener) {
		this.mStatusListener = statusListener;
	}

	/**
	 * Sets the time stamp to the current time when called.
	 * Programmers who subclass Item must call setTimeStamp() to 
	 * update the time where appropriate.
	 */
	protected void setTimeStamp() {
		mTimeStamp = new Date().getTime();
	}
	
	/*
	 * Why have this method?
	 * If we retrieve an item from a server, we should 
	 * set its time stamp to be the same as the copy on the server.
	 * This should avoid any unnecessary re-sync operations.
	 */
	public void setTimeStamp(long time){
		mTimeStamp = time;
	}
	
	public void setTimeStamp(Date date){
		mTimeStamp = date.getTime();
	}

	public void setUnitOfMeasure(String s) {
		if(! s.equals(mUnitOfMeasure)){
			mUnitOfMeasure = s;
			setTimeStamp();
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

	

}
