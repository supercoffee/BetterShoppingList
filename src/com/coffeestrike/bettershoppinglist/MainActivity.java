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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends FragmentActivity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback, 
	AddItemFragment.OnNewItemListener {
	
	private static final int MESSAGE_SENT = 1;
	private static final String MIME = "application/com.coffeestrike.bettershoppinglist";
	private static final String TAG = "MainActivity";
	
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

	//TODO replace references with calls to {@link ListManager}
	private ShoppingList mShoppingList;
    
    

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
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
	public ShoppingList getShoppingList() {
		return mShoppingList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.two_fragment_container);
		
//		mShoppingList = new ShoppingList(this);
		ListManager.getInstance(this).load();
		mShoppingList = ListManager.getInstance(this).getList(0);

		
		FragmentManager fm = getSupportFragmentManager();
		
		Fragment lowerFragment = fm.findFragmentById(R.id.bottom_frame);
		if (lowerFragment == null) {
			fm.beginTransaction()
					.add(R.id.bottom_frame, new ShoppingListFragment())
					.commit();
		}
		
//		Fragment upperFragment = fm.findFragmentById(R.id.top_frame);
//		if(upperFragment == null){
//			fm.beginTransaction()
//				.add(R.id.top_frame, new AddItemFragment())
//				.commit();
//		}
		
		
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if(mNfcAdapter != null){
			mNfcAdapter.setNdefPushMessageCallback(this, this);
			mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
			
		}
		
		Item.sDefaultUomList = getResources().getStringArray(R.array.units_of_measure);

	}

	@Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
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
	@Override
	public void onNewItem(Item i) {
		FragmentManager fm = getSupportFragmentManager();
		ShoppingListFragment shoppingListFragment = (ShoppingListFragment)fm.findFragmentById(R.id.bottom_frame);
		Intent intent = new Intent();
		intent.putExtra(Item.EXTRA_ITEM, i);
		shoppingListFragment.onActivityResult(ShoppingListFragment.NEW_ITEM, 
				Activity.RESULT_OK, intent);
	}
	@Override
	protected void onPause() {
		super.onPause();
		ListManager.getInstance(this).save();
	}
	
	

}
