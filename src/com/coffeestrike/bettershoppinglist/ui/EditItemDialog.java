package com.coffeestrike.bettershoppinglist.ui;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.R.array;
import com.coffeestrike.bettershoppinglist.R.id;
import com.coffeestrike.bettershoppinglist.R.layout;
import com.coffeestrike.bettershoppinglist.R.string;
import com.coffeestrike.bettershoppinglist.models.Item;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EditItemDialog extends DialogFragment {
	
	public static final String TAG = "EditItemDialog";
	public static final String EXTRA_POSITION = "com.coffeestrike.bettershoppinglist.EditItemDialog.position";
	
	private Item mItem;
	private int mPosition;
	protected CharSequence mPreviousDescription;
	protected int mPreviousQty;
	protected String mPreviousUom;

	private void sendResult(int resultCode){
		Fragment target = getTargetFragment();
		if(target == null){
			return;
		}
		if(mItem.getDescription() == null){
			return;
		}
		Intent i = new Intent();
		i.putExtra(Item.EXTRA_ITEM, mItem);
		if(mItem.getQty() < 1){
			mItem.setQty(1);
		}
//		i.putExtra(EXTRA_POSITION, mPosition);
		target.onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	public static EditItemDialog newInstance(Item item){
		Bundle args = new Bundle();
		args.putSerializable(Item.EXTRA_ITEM, item);
		EditItemDialog dialog = new EditItemDialog();
		dialog.setArguments(args);
		return dialog;
	}
	
//	public static EditItemDialog newInstance(Item item, int position){
//		Bundle args = new Bundle();
//		args.putSerializable(Item.EXTRA_ITEM, item);
//		args.putInt(EXTRA_POSITION, position);
//		EditItemDialog dialog = new EditItemDialog();
//		dialog.setArguments(args);
//		return dialog;
//	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		mItem = (Item)getArguments().getSerializable(Item.EXTRA_ITEM);

		mPreviousDescription = mItem.getDescription();
		mPreviousQty = mItem.getQty();
		mPreviousUom = mItem.getUnitOfMeasure();
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_item, null);
		
		if(getTargetRequestCode() == ShoppingListFragment.EDIT_ITEM){
			TextView t = (TextView) v.findViewById(R.id.item_uom);
			t.setText(R.string.edit_item);
		}

		EditText description = (EditText)v.findViewById(R.id.description_editText);
		description.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mItem.setDescription(s);
				Log.d(TAG, "description changed");
			}
			
		});
		
		if(mItem.getDescription() != null){
			description.setText(mItem.getDescription());
		}
		
		description.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
					((KeyboardEditText) view).setImeVisibility(hasFocus);
				}
		});
		
		EditText quantity = (EditText)v.findViewById(R.id.qty_editText);
		quantity.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					mItem.setQty(Integer.parseInt(s.toString()));
				} catch (NumberFormatException n) {
					mItem.setQty(1);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		if (mItem.getQty() != 0) {
			quantity.setText(String.valueOf(mItem.getQty()));
		}
		
		int uomResId = R.array.units_of_measure_imperial;
		if(PreferenceManager.getDefaultSharedPreferences(getActivity())
				.getBoolean("pref_metric", false)){
			uomResId = R.array.units_of_measure_metric;
		}
		Spinner spinner = (Spinner)v.findViewById(R.id.unit_spinner);
		ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter
				.createFromResource(getActivity(), uomResId, 
						android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinAdapter);
		String [] uomList = getActivity().getResources().getStringArray(uomResId);
		for(int i = 0; i < uomList.length; i++){
			if(uomList[i].compareTo(mItem.getUnitOfMeasure()) == 0){
				spinner.setSelection(i);
				break;
			}
		}
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View arg1,
					int pos, long arg3) {
				String s = (String) adapter.getItemAtPosition(pos);
				mItem.setUnitOfMeasure(s);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing here
			}
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		return builder.setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult(Activity.RESULT_OK);	
				
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(getTargetRequestCode() == ShoppingListFragment.EDIT_ITEM){
					mItem.setDescription(mPreviousDescription);
					mItem.setQty(mPreviousQty);
					mItem.setUnitOfMeasure(mPreviousUom);
				}
			}
		}).create();

	}

}
