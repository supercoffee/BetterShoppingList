package com.coffeestrike.bettershoppinglist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import com.coffeestrike.bettershoppinglist.Item.OnStatusChangedListener;

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
	
	//deprecated
//	private static final String FILENAME = "shoppinglist.json";
	
	private String mFilename;
//	private transient ShoppingListJSONSerializer mSerializer;
	private transient Context mAppContext;
	private String mListTitle;
	private UUID mListId;
//	private static ShoppingList sShoppingList;

	private Item mListDivider;
	

	public ShoppingList(Context appContext){
		mAppContext = appContext;
		mListId = UUID.randomUUID();
		mFilename = mListId.toString();
//		mSerializer = new ShoppingListJSONSerializer(mFilename, mAppContext);
	}
	
//	public static ShoppingList get(Context appContext){
//		if (sShoppingList != null){
//			return sShoppingList;
//		}
//		ShoppingList s = new ShoppingList(appContext.getApplicationContext());
//		s.loadList();
//		return s;
//	}
	protected ShoppingList(Context appContext, UUID id){
		mAppContext = appContext;
		mListId = id;
		mFilename = mListId.toString();
//		mSerializer = new ShoppingListJSONSerializer(mFilename, appContext);
	}
	
//	public static ShoppingList inflateFromFile(Context appContext, String uuidString){
//		ShoppingList s = null;
//		s = new ShoppingList(appContext, UUID.fromString(uuidString));
//		s.loadList();
//		return s;
//	}
	
//	public boolean loadList(){
//		try {
//			mSerializer.loadItems(ShoppingList.this);
//		} catch (JSONException e) {
//			Log.e(TAG, "Unable to load from JSON", e);
//			
//		} catch (IOException e) {
//			Log.e(TAG, "Unable to load list from storage", e);
//			e.printStackTrace();
//		}
//		return false;
//	}
	
//	public boolean saveList(){
//		try{
//			mSerializer.saveList(ShoppingList.this);
//			Log.d(TAG, "list saved to file");
//			return true;
//		}
//		catch(Exception e){
//			Log.e(TAG, "Error saving list to file: ", e);
//			return false;
//		}
//	}

	public int findListDivider(){
		return indexOf(mListDivider);
	}

	public UUID getListId() {
		return mListId;
	}

	public String getListTitle() {
		return mListTitle;
	}

	public void merge(ShoppingList incomingList) {
//		if(sShoppingList == null){
//			sShoppingList = new ShoppingList(activity.getApplicationContext());
//		}
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
//		int position = indexOf(i);
//		int listDividerPosition = findListDivider();
		/*
		 * If the list divider isn't found,
		 * we need to create one and place it at the bottom
		 * of the list.
		 */
		if(findListDivider() == -1){
			mListDivider = new Divider();
			add(mListDivider);
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
