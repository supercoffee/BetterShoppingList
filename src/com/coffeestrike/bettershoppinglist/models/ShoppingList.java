package com.coffeestrike.bettershoppinglist.models;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * @author Benjamin Daschel
 * Extension of ArrayList with ability to save and load from file
 *
 */
public class ShoppingList extends Observable implements Serializable, Item.OnStatusChangedListener{
	
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
		mItemList = new ArrayList<Item>();
		mListId = UUID.randomUUID();
		mGarbageQ = new ArrayDeque<Item>();
		//mFilename = mListId.toString();
	}
	
	protected ShoppingList(UUID id){
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
		if(item.getStatus() == 0){
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
//
//	@Override
//	public void add(int index, Item item) {
//		item.setStatusListener(this);
//		super.add(index, item);
//	}
//
//	@Override
//	public boolean add(Item item) {
//		item.setStatusListener(this);
//		return super.add(item);
//	}
//
//	@Override
//	public Item remove(int index) {
//		Item result =  super.remove(index);
//		if(size() == 1 && findListDivider() != -1){
//			clear();
//		}
//		return result;
//	}

	public ArrayList<Item> getBaseList() {
		return mItemList;
	}
	
	public void add(int index, Item item){
		item.setStatusListener(this);
		mItemList.add(index, item);
		notifyObservers();
	}
	
	public void add(Item item){
		mItemList.add(item);
		notifyObservers();
	}
	
	public Item remove(int index){
		Item result = mItemList.remove(index);
		
		int divIndex = findListDivider();
		
		if(divIndex == mItemList.size() -1 && divIndex != -1){
			 mItemList.remove(divIndex);
		}
		notifyObservers();
		
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
		notifyObservers();
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


}
