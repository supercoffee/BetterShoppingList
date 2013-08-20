package com.coffeestrike.bettershoppinglist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class ShoppingListFragment extends ListFragment {
	
	public static String TAG = "ShoppingListFragment";
	private ArrayList<Item> mItemList;
	private static final int NEW_ITEM = 0;
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
		Log.d(TAG, "options menu inflated");
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.app_name);
		
		mItemList =  ShoppingList.get(getActivity()).getList();
		
		setListAdapter(new ShoppingListAdapter(mItemList));
		setRetainInstance(true);
	}
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState){

		View v = inflater.inflate(R.layout.shopping_list, null);
		
		Button b = (Button)v.findViewById(R.id.addItem_button);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				newListItem();
			}
		});

		return v;
	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.add_button:
				newListItem();	
		}
		return false;
	}
	
	private void newListItem(){
		FragmentManager fm = getActivity().getSupportFragmentManager();
		EditItemDialog newItem = EditItemDialog.newInstance(new Item());
		newItem.setTargetFragment(this, NEW_ITEM);
		newItem.show(fm, TAG);
	}
	
	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent data){
		Log.d(TAG, "onActivityResult");
		if(resultCode != Activity.RESULT_OK){	
			return;
		}
		if(requestCode == NEW_ITEM){
			mItemList.add( (Item) data.getSerializableExtra(EditItemDialog.EXTRA_ITEM) );
			((ShoppingListAdapter)getListAdapter()).notifyDataSetChanged();
		}

	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		((ShoppingListAdapter)getListAdapter()).notifyDataSetChanged();
		Log.d(TAG, "onResume()");
	}



	private class ShoppingListAdapter extends ArrayAdapter<Item> {

		public ShoppingListAdapter(ArrayList<Item> list){
			super(getActivity(), 0, list);
		}
		
		@Override
		public View getView (int position, View convertView, ViewGroup parent){
			if(convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.item, null);
			}
			Item i = getItem(position);
			CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.item_checkBox);
			TextView itemText = (TextView) convertView.findViewById(R.id.item_text);
			TextView itemQty = (TextView) convertView.findViewById(R.id.item_qty);
			checkbox.setChecked(i.getStatus() == 1);
			itemText.setText(i.getDescription());
			itemQty.setText(String.valueOf(i.getQty()));
			
			return convertView;
		}
		
		
	}
	

}
