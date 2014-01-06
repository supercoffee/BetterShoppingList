package com.coffeestrike.bettershoppinglist.extra;

/**
 * Classes implementing this method are capable of receiving a 
 * callback when the SyncManager finishes a complete synchronization 
 * of the list.
 * @author Benjamin Daschel
 *
 */
public interface SyncFinishedListener {
	public void onSyncFinished();
}
