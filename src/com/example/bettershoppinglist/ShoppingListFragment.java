package com.example.bettershoppinglist;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

	}
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState){
		if(container == null){
			
			
			return  null;
		}
		View v = inflater.inflate(R.layout.shopping_list, null);
		if(v != null){
			ListView list = (ListView) v.findViewById(android.R.id.list);
			list.setEmptyView(inflater.inflate(R.layout.empty_view, container));
		}
		return v;
	
	}
	
//	@Override
//	public void onStart(){
//		super.onStart();
//		setEmptyText(getActivity().getText(R.string.empty_text));
//	}

}
