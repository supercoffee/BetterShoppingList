//package com.coffeestrike.bettershoppinglist;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.TextView.OnEditorActionListener;
//
//public class AddItemFragment extends Fragment {
//
//	protected static final String TAG = "AddItemFragment";
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View v = inflater.inflate(R.layout.quick_add_bar, null);
//		
//		EditText addItem = (EditText) v.findViewById(R.id.editText_new_item);
//		addItem.setOnEditorActionListener(new OnEditorActionListener(){
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if(actionId == EditorInfo.IME_ACTION_DONE){
//					//TODO add a new item to the list
//					addNewItem();
//					Log.d(TAG, "Done button pressed");
//					return true;
//				}
//				return false;
//			}
//			
//		});
//		
//		
//		Button addButton = (Button) v.findViewById(R.id.button_add_item);
//		
//		return v;
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setHasOptionsMenu(false);
//	}
//	
//	public  void addNewItem(){
//		EditText editText = (EditText) getView().findViewById(R.id.editText_new_item);
//		ShoppingList.get(getActivity()).add(new Item(editText.getText().toString()));
//		FragmentManager fm = getActivity().getSupportFragmentManager();
//		ShoppingListFragment list = (ShoppingListFragment) fm.findFragmentById(R.id.bottom_frame);
//		((ArrayAdapter)list.getListAdapter()).notifyDataSetChanged();
//	}
//	
//	
//
//	
//	
//}
