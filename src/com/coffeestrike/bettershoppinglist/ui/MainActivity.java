package com.coffeestrike.bettershoppinglist.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.database.DataSetObserver;
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
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.extra.ListManager;
import com.coffeestrike.bettershoppinglist.models.Item;
import com.coffeestrike.bettershoppinglist.models.ShoppingList;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends FragmentActivity implements OnNavigationListener, CreateNdefMessageCallback, OnNdefPushCompleteCallback, 
	AddItemFragment.OnNewItemListener{
	
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
	
	private SpinnerAdapter mNavAdapter = new SpinnerAdapter(){

		@Override
		public int getCount() {
			return ListManager.getInstance(MainActivity.this).getAllTitles().length;
		}

		@Override
		public Object getItem(int position) {
			return ListManager.getInstance(MainActivity.this).getAllTitles()[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
	
	};

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
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		
//		mShoppingList = new ShoppingList(this);
		ListManager.getInstance(this).load();
		mShoppingList = ListManager.getInstance(this).getList(0);
		
		

		
		FragmentManager fm = getSupportFragmentManager();
		
		Fragment lowerFragment = fm.findFragmentById(R.id.top_frame);
		if (lowerFragment == null) {
			fm.beginTransaction()
					.add(R.id.top_frame, new ShoppingListFragment())
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
		/*
		 * Populate the list of available units of measure for the Item class
		 * It's done here because it requires inflation from a resource file,
		 * and passing this activity all the way to the Item class is bad OOP.
		 */
		int uomResId = R.array.units_of_measure_imperial;
		if(PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("pref_metric", false)){
			uomResId = R.array.units_of_measure_metric;
		}
		
		Item.sDefaultUomList = getResources().getStringArray(uomResId);

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
			ShoppingListFragment fragment = (ShoppingListFragment) fm.findFragmentById(R.id.top_frame);
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
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void requestBackup(){
		BackupManager bm = new BackupManager(this);
		bm.dataChanged();
	}
	

}
