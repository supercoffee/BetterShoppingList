package com.coffeestrike.bettershoppinglist.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.coffeestrike.bettershoppinglist.R;
import com.coffeestrike.bettershoppinglist.extra.ListManager;
import com.coffeestrike.bettershoppinglist.models.Item;
import com.coffeestrike.bettershoppinglist.models.ItemSyncObserver;
import com.coffeestrike.bettershoppinglist.models.ShoppingList;


/**
 * 
 *  * Primary interface for the application.
 * Responsible for displaying the current list of items,
 * and controlling the flow of items in and out of the list.
 * 
 * @author Benjamin Daschel
 *
 */
public class ShoppingListFragment extends ListFragment {
	
	public static String TAG = "ShoppingListFragment";
	private ShoppingList mShoppingList;
	private String mServerURL;
	private String mRemoteListPath;
	private ShoppingList mRemoteList;
	private Context mAppContext;
	private ShoppingListAdapter mListAdapter;
	private ItemSyncObserver mItemSyncObserver;
	public static final int NEW_ITEM = 0;
	public static final int EDIT_ITEM = 1;
	@SuppressWarnings("unused")
	private static final String EXTRA_ITEM = "com.coffeestrike.bettershoppinglist.ShoppingListFragment";
	
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
		Log.d(TAG, "options menu inflated");
	}
	
	
	@Override
	public void onPrepareOptionsMenu(Menu menu){
		super.onPrepareOptionsMenu(menu);
		/*
		 * This is the fix to the list sorting crash.
		 * When the divider exists in the list, sorting is disabled.
		 * It doesn't make sense to be able to sort the list in that state anyways.
		 */
		MenuItem sortAlpha = menu.findItem(R.id.sort_alpha);
		if(mShoppingList.findListDivider() != -1){
			sortAlpha.setEnabled(false);
		}
		else{
			sortAlpha.setEnabled(true);
		}
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mAppContext = getActivity().getApplicationContext();
		mItemSyncObserver = new ItemSyncObserver(mAppContext);
//		mShoppingList = ((MainActivity)getActivity()).getShoppingList();
		mShoppingList = ListManager.getInstance(mAppContext).getList(0);
		mListAdapter = new ShoppingListAdapter(mShoppingList.getBaseList());
		setListAdapter(mListAdapter);
		getActivity().setTitle(mShoppingList.getListTitle());
		setRetainInstance(true);
	}
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState){

		View v = inflater.inflate(R.layout.shopping_list, null);
		
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
			/*
			 * (non-Javadoc)
			 * @see android.view.ActionMode.Callback#onPrepareActionMode(android.view.ActionMode, android.view.Menu)
			 * Action Mode listener
			 */
			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode arg0) {}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context_action_bar, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
				ShoppingListAdapter adapter = (ShoppingListAdapter)getListAdapter();
				switch(menuItem.getItemId()){
					case R.id.menu_item_delete:
						
						for(int position = adapter.getCount(); position >= 0; position--){
							if(getListView().isItemChecked(position)){
								mShoppingList.remove(position);
							}
						}
						mode.finish();
						refresh();
						return true;
					case R.id.menu_item_move:
						for(int position = adapter.getCount(); position >= 0; position--){
							if(getListView().isItemChecked(position)){
								adapter.getItem(position).setStatus(1);
							}
						}
						mode.finish();
						refresh();
					default:
						return false;
				}

			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode arg0, int arg1, long arg2,
					boolean arg3) {
			}
		});
		
		Button b = (Button)v.findViewById(R.id.addItem_button);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showEditItemDialog(new Item());
			}
		});

		return v;
	
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 * Selection from options menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.button_add_item:
				showEditItemDialog(new Item());
				return true;
			case R.id.sort_alpha:
				mShoppingList.sortAlpha();
				refresh();
				return true;
			case R.id.clear_all:
				mShoppingList.clear();
				refresh();
				return true;
			case R.id.action_settings:
				Intent intent = new Intent(getActivity(), SettingsActivity.class);
				startActivity(intent);
				
		}
		
		return false;
	}
	
	
	/**
	 * @param requestCode indicates the purpose of the original request
	 * @param resultCode indicates if the requested action completed successfully
	 * @param data {@link Intent} containing data to be processed based on requestCode
	 */
	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent data){
		Log.d(TAG, "onActivityResult");
		if(resultCode != Activity.RESULT_OK){	
			return;
		}
		Item item = (Item)data.getSerializableExtra(Item.EXTRA_ITEM);
		
		/*
		 * Attaching the observer here handles all the cases where new 
		 * Items are created from UI interaction.
		 */
		if(!mShoppingList.contains(item)){
			item.addObserver(mItemSyncObserver);
			mShoppingList.add(0, item);
		}
		refresh();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "list item "+ position + " clicked.");
		super.onListItemClick(l, v, position, id);
		Item item = (Item) l.getItemAtPosition(position);
		showEditItemDialog(item);
	}
	
	private void showEditItemDialog(Item item){
		FragmentManager fm = getActivity().getSupportFragmentManager();
		EditItemDialog editItem = EditItemDialog.newInstance(item);
		editItem.setTargetFragment(this, EDIT_ITEM);
		editItem.show(fm, TAG);
	}

	/**
	 * @author Benjamin Daschel
	 *Simple bridge class between ShoppingList model data and list layout views.
	 *
	 */
	private class ShoppingListAdapter extends ArrayAdapter<Item> {

		public ShoppingListAdapter(ArrayList<Item> list){
			super(getActivity(), 0, list);
		}
		
		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return !(getItem(position).isDivider());
		}

		@Override
		public View getView (int position, View convertView, ViewGroup parent){
			final Item item = getItem(position);
			if(item.isDivider()){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_divider, null);
				convertView.setOnClickListener(null);
				convertView.setOnLongClickListener(null);
				convertView.setClickable(false);
			}
			else{
				if(convertView == null || convertView.getId() != R.layout.item){
					convertView = getActivity().getLayoutInflater().inflate(R.layout.item, null);	
				}
				
				CheckBox checkbox = (CheckBox) convertView
						.findViewById(R.id.item_checkBox);
				checkbox.setChecked(item.getStatus() == 1);
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							item.setStatus(1);
						} 
						else {
							item.setStatus(0);
						}
						notifyDataSetChanged();

					}
				});
				TextView itemText = (TextView) convertView
						.findViewById(R.id.item_text);
				TextView itemQty = (TextView) convertView
						.findViewById(R.id.item_qty);
				TextView itemUom = (TextView) convertView
						.findViewById(R.id.item_uom);
				
				itemText.setText(item.getDescription());
				itemQty.setText(String.valueOf(item.getQty()));
				itemUom.setText(item.getUnitOfMeasure());
			}
			
			return convertView;
		}
	}

	/**
	 * Convenience method to refresh the ListView when the data set changes.
	 */
	public void refresh() {
		((ShoppingListAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	
	private boolean syncRemoteEnabled(){
		return PreferenceManager.getDefaultSharedPreferences(mAppContext)
				.getBoolean(SettingsActivity.KEY_SYNC_REMOTE, false);
	}
	
	/**
	 * Use this method to synchronize the list
	 * in JSON format to a remote server.
	 * 
	 */
	private void syncRemote(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
		 
		
		mServerURL = prefs.getString(SettingsActivity.KEY_SERVER_URL_KEY,
				mAppContext.getResources().getString(R.string.default_server_url));
		mRemoteListPath = prefs.getString(SettingsActivity.KEY_SERVER_LIST_PATH, 
				mAppContext.getResources().getString(R.string.default_server_list_path));
		

	}
	
//	private class FetchJSONList extends AsyncTask<URL, Void, ShoppingList>{
//		
//		@Override
//		protected ShoppingList doInBackground(URL... params) {
//			StringBuilder builder = new StringBuilder();
//			JSONObject [] jArray = null;
//			ShoppingList shoppingList = new ShoppingList();
//			try{
//				URL url = params[0];
//				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//				InputStream iStream = connection.getInputStream();
//				
//				BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
//				String s;
//				while( (s = reader.readLine()) != null){
//					builder.append(s);
//				}
//				
//				reader.close();
//				connection.disconnect();
//				
//			} catch(Exception e){
//				e.printStackTrace();
//			}
//			
//			try{
//				JSONObject results = new JSONObject(builder.toString());
//				jArray = JSONResults.splitResults(results);
//				
//				if(jArray != null){
//					for(JSONObject j: jArray){
//						shoppingList.add(JSONItemParser.readJSONObject(j));
//					}
//				}
//				
//			}catch(JSONException e){
//				e.printStackTrace();
//			}
//			catch(Exception e){
//				e.printStackTrace();
//			}
//			return shoppingList;
//		}
//		
//		@Override
//		protected void onPostExecute(ShoppingList list){
//			mShoppingList.merge(list);
//			ShoppingListFragment.this.refresh();
//			Log.d(TAG, String.format("Fetched %d items", list.size()) );
//		}
//
//		
//	}
	
}
