package com.example.sspdim.database

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.sqlite.db.SimpleSQLiteQuery
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

private const val TAG = "BackupAndRestore"

class BackupAndRestore(context: Context) {

    private var context : Context = context
    @RequiresApi(Build.VERSION_CODES.O)
    private var database : Path = Paths.get(context.getDatabasePath("app_database").path)
    @RequiresApi(Build.VERSION_CODES.O)
    private var backupFolder : Path = Paths.get(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toURI())
    @RequiresApi(Build.VERSION_CODES.O)
    var backupFile : Path = Paths.get("$backupFolder/sspdim-backup")

    @RequiresApi(Build.VERSION_CODES.O)
    fun backup() {
        try {
            AppDatabase.getDatabase(context).chatMessageDao()
                .checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
            Files.copy(database, backupFile, StandardCopyOption.REPLACE_EXISTING)
            Toast.makeText(context, "Backup store at $backupFile", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            Log.d(TAG, "Error taking backup")
            e.printStackTrace()
            Toast.makeText(context, "Error taking backup!", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun restore() {
        try {
            AppDatabase.getDatabase(context).close()
            Files.copy(backupFile, database, StandardCopyOption.REPLACE_EXISTING)
            Toast.makeText(context, "Backup restored. Please restart the app", Toast.LENGTH_SHORT)
                .show()
        }
        catch (e: Exception) {
            Log.d(TAG, "Error restoring backup")
            e.printStackTrace()
            Toast.makeText(context, "Error restoring backup!", Toast.LENGTH_SHORT).show()
        }
    }
}