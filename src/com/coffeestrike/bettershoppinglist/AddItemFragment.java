package com.coffeestrike.bettershoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentIntegratorSupportV4;
import com.google.zxing.integration.android.IntentResult;

public class AddItemFragment extends Fragment {

	protected static final String TAG = "AddItemFragment";
	private OnNewItemListener mActivityCallback;
	private EditText mEditText;
	
	/*
	 * Interface for communicating with the parent activity
	 */
	public interface OnNewItemListener{
		public void onNewItem(Item i);
	}
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		
		try{
			mActivityCallback = (OnNewItemListener) activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + "must implement OnNewItemListener");
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.quick_add_bar, null);
		
		mEditText = (EditText) v.findViewById(R.id.editText_new_item);
		mEditText.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView t, int actionId,
					KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE){
					if(mEditText.length() != 0){
						newItem();
						return true;
					}
				}
				return false;
			}
			
		});
		
		
		Button addButton = (Button) v.findViewById(R.id.button_add_item);
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				newItem();
			}
		});
		
		Button scanButton = (Button)v.findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener(){
			/*
			 *Eventually, this will fire up an intent to start
			 * a barcode scanner.
			 */
			@Override
			public void onClick(View v) {
				IntentIntegratorSupportV4 integrator = new IntentIntegratorSupportV4(AddItemFragment.this);
				integrator.initiateScan();
			}
			
		});
		
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}

	private void newItem() {
		if(mEditText.length() != 0){
			Item i = new Item(mEditText.getText().toString());
			mEditText.setText("");
			mActivityCallback.onNewItem(i);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		  if (scanResult != null) {
		    // handle scan result
		  }
	}
	
	

	
	
}
