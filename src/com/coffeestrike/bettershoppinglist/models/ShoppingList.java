package com.coffeestrike.bettershoppinglist.models;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Observable;
import java.util.UUID;

import android.os.Bundle;

/**
 * @author Benjamin Daschel
 *
 */
public class ShoppingList extends Observable implements Iterable<Item>, Serializable, Item.OnStatusChangedListener{
	
	public static final String EXTRA_OPERATION = "operation";
	public static final String EXTRA_DATA = "item";
	public static final int ADD = 1;
	public static final int DELETE = 2;
	public static final int CLEAR = 3;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -62369575092508088L;

	//private String mFilename;
	private String mListTitle = "My List";
	private UUID mListId;
	private ArrayList<Item> mItemList;
	
	private ArrayDeque<Item> mGarbageQ;

	
	public ShoppingList(){
		super();
		mItemList = new ArrayList<Item>();
		mListId = UUID.randomUUID();
		mGarbageQ = new ArrayDeque<Item>();
		//mFilename = mListId.toString();
	}
	
	protected ShoppingList(UUID id){
		super();
		mItemList = new ArrayList<Item>();
		mListId = id;
		mGarbageQ = new ArrayDeque<Item>();
		//mFilename = mListId.toString();
	}

	public int findListDivider(){
		return mItemList.indexOf(new Divider());
	}

	public UUID getListId() {
		return mListId;
	}

	public String getListTitle() {
		return mListTitle;
	}

	public void merge(ShoppingList incomingList) {
		
		for(Item item : incomingList.mItemList){
			if(! mItemList.contains(item)){
				mItemList.add(item);
			}
		}
		
	}

	public void setListTitle(String listTitle) {
		mListTitle = listTitle;
	}

	public void sortAlpha() {
		Collections.sort(mItemList);
	}


	@Override
	public void onStatusChanged(Item item) {
		/*
		 * If the list divider isn't found,
		 * we need to create one and place it at the bottom
		 * of the list.
		 */
		if(findListDivider() == -1){
			mItemList.add(new Divider());
		}
		
		/*The check box has been cleared
		 * restore the item to the upper part of the list
		 */
		if(! item.isChecked()){
			mItemList.remove(item);
			mItemList.add(findListDivider(), item);
		}
		/*the check box has been checked
		 * move the item to the lower part of the list
		 */
		else{
			mItemList.remove(item);
			mItemList.add(item);
		}
		
		/*
		 * Now we should check to see if the divider is the
		 * very last element in the list.
		 * If it is, we should remove it because there is nothing in
		 * the cart
		 */
		int p = findListDivider();
		if(p == (mItemList.size() - 1)){
			mItemList.remove(p);
		}
		
	}

	public ArrayList<Item> getBaseList() {
		return mItemList;
	}
	
	public void add(int index, Item item){
		item.setStatusListener(this);
		mItemList.add(index, item);
		notifyObserversAddItem(item);
	}
	
	public void add(Item item){
		item.setStatusListener(this);
		mItemList.add(item);
		notifyObserversAddItem(item);
	}
	
	private void notifyObserversAddItem(Item item){
		Bundle bundle = new  Bundle();
		bundle.putInt(EXTRA_OPERATION, ADD);
		bundle.putSerializable(EXTRA_DATA, item);
		notifyObservers(bundle);
	}
	
	public Item remove(int index){
		Item result = mItemList.remove(index);
		
		int divIndex = findListDivider();
		
		if(divIndex == mItemList.size() -1 && divIndex != -1){
			 mItemList.remove(divIndex);
		}
		
		Bundle bundle = new  Bundle();
		bundle.putInt(EXTRA_OPERATION, DELETE);
		bundle.putSerializable(EXTRA_DATA, result);
		notifyObservers(bundle);
		
		return result;
	}

	public boolean contains(Item item) {
		return mItemList.contains(item);
	}
	
	
	/*
	 * Place the Items in a deletion queue.
	 * The observer needs to know the IDs of the
	 * items so the server knows which IDs to remove.
	 */
	public void clear() {
		for(Item item : mItemList){
			mGarbageQ.add(item);
		}
		mItemList.clear();
		
		Bundle bundle = new  Bundle();
		bundle.putInt(EXTRA_OPERATION, CLEAR);
		notifyObservers(bundle);
	}

	public int size() {
		return mItemList.size();
	}

	/**
	 * Get "deleted" items back.
	 * @return deleted items in FIFO order
	 */
	public Item pollGarbageQueue(){
		return mGarbageQ.poll();
	}
	
	public int garbageQueueSize(){
		return mGarbageQ.size();
	}
	
	public boolean isGarbageEmpty(){
		return mGarbageQ.size() == 0;
	}

	@Override
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	@Override
	public void notifyObservers(Object data) {
		setChanged();
		super.notifyObservers(data);
	}

	@Override
	public Iterator<Item> iterator() {
		return mItemList.iterator();
	}

	
	

}
