package com.example.bettershoppinglist;

import java.io.Serializable;
import java.util.UUID;

public class Item implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8439535938970924273L;
	private int mStatus; //0 = default, 1 = found, 2 = find later
	private String mDescription;
	private int mQty;
	private UUID mId;
	
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
		mStatus = status;
	}
	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String description) {
		mDescription = description;
	}
	public int getQty() {
		return mQty;
	}
	public void setQty(int qty) {
		mQty = qty;
	}
	
	
	
	

}
