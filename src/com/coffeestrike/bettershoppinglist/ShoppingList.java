package com.coffeestrike.bettershoppinglist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import android.content.Context;

/**
 * @author Benjamin Daschel
 * Extension of ArrayList with ability to save and load from file
 *
 */
public class ShoppingList extends ArrayList<Item> implements Item.OnStatusChangedListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -62369575092508088L;

	private static final String TAG = "ShoppingList";

	private String mFilename;
	private transient Context mAppContext;
	private String mListTitle = "My List";
	private UUID mListId;

	private Item mListDivider;
	

	public ShoppingList(Context appContext){
		mAppContext = appContext;
		mListId = UUID.randomUUID();
		mFilename = mListId.toString();
	}
	
	protected ShoppingList(Context appContext, UUID id){
		mAppContext = appContext;
		mListId = id;
		mFilename = mListId.toString();
	}

	public int findListDivider(){
		return indexOf(new Divider());
	}

	public UUID getListId() {
		return mListId;
	}

	public String getListTitle() {
		return mListTitle;
	}

	public void merge(ShoppingList incomingList) {
		for(Item item : incomingList){
			if(! this.contains(item)){
				add(item);
			}
		}
		
	}

	public void setListTitle(String listTitle) {
		mListTitle = listTitle;
	}

	public void sortAlpha() {
		Collections.sort(ShoppingList.this);
	}

	@Override
	public void onStatusChanged(Item item) {
		/*
		 * If the list divider isn't found,
		 * we need to create one and place it at the bottom
		 * of the list.
		 */
		if(findListDivider() == -1){
			add(new Divider());
		}
		
		/*The check box has been cleared
		 * restore the item to the upper part of the list
		 */
		if(item.getStatus() == 0){
			remove(item);
			add(findListDivider(), item);
		}
		/*the check box has been checked
		 * move the item to the lower part of the list
		 */
		else{
			remove(item);
			add(item);
		}
		
		/*
		 * Now we should check to see if the divider is the
		 * very last element in the list.
		 * If it is, we should remove it because there is nothing in
		 * the cart
		 */
		int p = findListDivider();
		if(p == (this.size() - 1)){
			remove(p);
		}
		
	}

	@Override
	public void add(int index, Item item) {
		item.setStatusListener(this);
		super.add(index, item);
	}

	@Override
	public boolean add(Item item) {
		item.setStatusListener(this);
		return super.add(item);
	}


}
