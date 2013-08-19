package com.example.bettershoppinglist;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.TextView;


public class ShoppingListFragment extends ListFragment {
	
	public static String TAG = "ShoppingListFragment";
	private ArrayList<Item> mItemList;
	
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
//		setRetainInstance(true);
	}
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState){

		View v = inflater.inflate(R.layout.shopping_list, null);

		return v;
	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.add_button:
				FragmentManager fm = getActivity().getSupportFragmentManager();
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				LayoutInflater inflater = getActivity().getLayoutInflater();
				builder.setView(inflater.inflate(R.layout.new_item_dialog, null))
					.setPositiveButton(R.string.add_item, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							
						}
					})
					.setNegativeButton(R.string.lorem_ipsum, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							
						}
					});
				builder.show();
				
		}
		return false;
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
