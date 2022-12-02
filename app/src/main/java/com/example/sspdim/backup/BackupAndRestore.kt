package com.example.sspdim.backup

import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.sspdim.database.AppDatabase
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

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
        AppDatabase.getDatabase(context).chatMessageDao().checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
        Files.copy(database, backupFile, StandardCopyOption.REPLACE_EXISTING)
        Toast.makeText(context,"Backup store at $backupFile", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun restore() {
        AppDatabase.getDatabase(context).close()
        Files.copy(backupFile, database, StandardCopyOption.REPLACE_EXISTING)
        Toast.makeText(context,"Backup restored. Please restart the app", Toast.LENGTH_SHORT).show()
    }
}