package com.coffeestrike.bettershoppinglist.extra;

import com.coffeestrike.bettershoppinglist.models.ListManager;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

public class ListBackupHelper extends BackupAgentHelper {
	
	static final String LIST_FILENAME = ListManager.FILENAME;
	
	static final String FILES_BACKUP_KEY = "user-files";
	
	@Override
	public void onCreate(){
		FileBackupHelper fb = new FileBackupHelper(this, LIST_FILENAME);
		addHelper(FILES_BACKUP_KEY, fb);
	}

}
