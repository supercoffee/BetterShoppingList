package com.coffeestrike.bettershoppinglist.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.models.Item;

public class EditItemDialog extends DialogFragment {
	
	public static final String TAG = "EditItemDialog";
	
	private Item mItem;

	private void sendResult(int resultCode){
		Fragment target = getTargetFragment();
		
		if(target == null){
			return;
		}
		Intent i = null;
		if (resultCode == Activity.RESULT_OK) {
			if (mItem.getDescription() == null) {
				return;
			}
			i = new Intent();
			i.putExtra(Item.EXTRA_ITEM, mItem);
			if (mItem.getQty() < 1) {
				mItem.setQty(1);
			}
		}
		target.onActivityResult(getTargetRequestCode(), resultCode, i);
	}
	
	public static EditItemDialog newInstance(Item item){
		Bundle args = new Bundle();
		args.putSerializable(Item.EXTRA_ITEM, item);
		EditItemDialog dialog = new EditItemDialog();
		dialog.setArguments(args);
		return dialog;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		mItem = (Item)getArguments().getSerializable(Item.EXTRA_ITEM);

		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_item, null);

		/*
		 * Set up the description text box
		 */
		final EditText description = (EditText)v.findViewById(R.id.description_editText);
		
		if(mItem.getDescription() != null){
			description.setText(mItem.getDescription());
		}
		
		description.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
					((KeyboardEditText) view).setImeVisibility(hasFocus);
				}
		});

		
		/*
		 * Set up the quantity text box
		 */
		final EditText quantity = (EditText)v.findViewById(R.id.qty_editText);

		if (mItem.getQty() != 0) {
			quantity.setText(String.valueOf(mItem.getQty()));
		}
		
		/*
		 * Set up the spinner for units of measure
		 */
		int uomResId = R.array.units_of_measure_imperial;
		if(PreferenceManager.getDefaultSharedPreferences(getActivity())
				.getBoolean("pref_metric", false)){
			uomResId = R.array.units_of_measure_metric;
		}
		final Spinner uomSpinner = (Spinner)v.findViewById(R.id.unit_spinner);
		ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter
				.createFromResource(getActivity(), uomResId, 
						android.R.layout.simple_spinner_dropdown_item);
		uomSpinner.setAdapter(spinAdapter);
		String [] uomList = getActivity().getResources().getStringArray(uomResId);
		for(int i = 0; i < uomList.length; i++){
			if(uomList[i].compareTo(mItem.getUnitOfMeasure()) == 0){
				uomSpinner.setSelection(i);
				break;
			}
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		return builder.setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//adjust the items values before continuing
				mItem.setDescription(description.getText());
				mItem.setQty(Integer.parseInt(quantity.getText().toString()));
				mItem.setUnitOfMeasure((String) uomSpinner.getSelectedItem());
				sendResult(Activity.RESULT_OK);	
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(getTargetRequestCode() == ShoppingListFragment.EDIT_ITEM){
					sendResult(Activity.RESULT_CANCELED);
				}
			}
		}).create();

	}

}
