package com.example.bettershoppinglist;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

@SuppressWarnings("unused")
public class ShoppingListFragment extends ListFragment {
	
	private ArrayList<Item> mItemList;
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.app_name);
		
		mItemList =  ShoppingList.get(getActivity()).getList();
		
		setListAdapter(new ShoppingListAdapter(mItemList));
	}
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState){
//		View v = super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.shopping_list, null);
//		if(v != null){
//			AdapterView<ShoppingListAdapter> list = (AdapterView<ShoppingListAdapter>) v.findViewById(android.R.id.list);
//			list.setEmptyView(inflater.inflate(R.layout.empty_view, container));
//		}
		return v;
	
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
			checkbox.setChecked(i.getStatus() == 1);
			itemText.setText(i.getDescription());
			
			return convertView;
		}
		
		
	}
	

}
