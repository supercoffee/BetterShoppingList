package com.coffeestrike.bettershoppinglist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardEditText extends EditText {

	
	private Runnable mShowImeRunnable = new Runnable() {
	    public void run() {
	        InputMethodManager imm = (InputMethodManager) getContext()
	                .getSystemService(Context.INPUT_METHOD_SERVICE);

	        if (imm != null) {
	            imm.showSoftInput(KeyboardEditText.this,0);
	        }
	    }
	};
	
	public KeyboardEditText(Context context) {
		super(context);
	}

	public KeyboardEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KeyboardEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	public void setImeVisibility(final boolean visible) {
	    if (visible) {
	        post(mShowImeRunnable);
	    } else {
	        removeCallbacks(mShowImeRunnable);
	        InputMethodManager imm = (InputMethodManager) getContext()
	                .getSystemService(Context.INPUT_METHOD_SERVICE);

	        if (imm != null) {
	            imm.hideSoftInputFromWindow(getWindowToken(), 0);
	        }
	    }
	}

}
