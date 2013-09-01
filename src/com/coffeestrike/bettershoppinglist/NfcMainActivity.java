package com.coffeestrike.bettershoppinglist;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.annotation.TargetApi;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;



@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class NfcMainActivity extends MainActivity implements CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback{

	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case MESSAGE_SENT:
	                Toast.makeText(getApplicationContext(), "List sent!", Toast.LENGTH_LONG).show();
	                break;
	            }
	        }
	    };
	protected NfcAdapter mNfcAdapter;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD){
			return null;
		}
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] shoppingListBytes = null;
			try {
				out = new ObjectOutputStream(byteStream);
				out.writeObject(mShoppingList);
				shoppingListBytes = byteStream.toByteArray();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "", e);
				}
				try {
					byteStream.close();
				} catch (IOException e) {
					Log.e(TAG, "", e);
				}
			}
		NdefMessage msg = new NdefMessage(NdefRecord.createMime(MIME, shoppingListBytes));
		
		return msg;
	}

	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
	    // A handler is needed to send messages to the activity when this
	    // callback occurs, because it happens from a binder thread
	    mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()) ) {
            processIntent(getIntent());
        }
	}

	private void processIntent(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
	            NfcAdapter.EXTRA_NDEF_MESSAGES);
		if(rawMsgs != null){
			Log.d(TAG, "NFC message received");
		}
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		byte [] incomingList = msg.getRecords()[0].getPayload();
		
		ByteArrayInputStream byteStream = new ByteArrayInputStream(incomingList);
		ObjectInput iStream = null;
		try{
			iStream = new ObjectInputStream(byteStream);
			Object o = iStream.readObject();
			mShoppingList.merge((ShoppingList) o);
			FragmentManager fm = getSupportFragmentManager();
			ShoppingListFragment fragment = (ShoppingListFragment) fm.findFragmentById(R.id.bottom_frame);
			if(fragment != null){
				fragment.refresh();
			}
			
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

		

	}
