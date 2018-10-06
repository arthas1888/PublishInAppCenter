package co.edu.aulamatriz.dbapplication.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.content.UriMatcher
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import co.edu.aulamatriz.dbapplication.databases.DBHelper
import android.database.sqlite.SQLiteDatabase




class ExampleProvider : ContentProvider() {
    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    private var myDB: DBHelper? = null

    companion object {
        val AUTHORITY = "co.edu.aulamatriz.dbapplication.providers.ExampleProvider"
        val DATUM = 1
        val DATUM_ID = 2
    }

    init {
        sUriMatcher.addURI(AUTHORITY, DBHelper.TABLE_1, DATUM)
        sUriMatcher.addURI(AUTHORITY, "${DBHelper.TABLE_1}/#", DATUM)
    }

    override fun onCreate(): Boolean {
        myDB = DBHelper(context)
        return true
    }


    override fun query(uri: Uri?,
                       columns: Array<out String>?,
                       selection: String?,
                       selectionArgs: Array<out String>?,
                       orderBy: String?): Cursor {

        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = DBHelper.TABLE_1

        val uriType = sUriMatcher.match(uri)

        when (uriType) {
            DATUM_ID -> queryBuilder.appendWhere(DBHelper.COLUMN_ID + "="
                    + uri!!.lastPathSegment)
            DATUM -> {
            }
            else -> throw IllegalArgumentException("Unknown URI")
        }

        val cursor = queryBuilder.query(myDB?.readableDatabase,
                columns, selection, selectionArgs, null, null,
                orderBy)
        cursor.setNotificationUri(context.contentResolver,
                uri)
        return cursor
    }

    override fun bulkInsert(uri: Uri?, values: Array<out ContentValues>?): Int {

        var numInserted = 0
        var table: String
        val uriType = sUriMatcher.match(uri)
        val sqlDB = myDB!!.writableDatabase

        when (uriType) {
            DATUM -> {
                table = DBHelper.TABLE_1
            }
            else -> throw IllegalArgumentException("Unknown URI")
        }

        sqlDB.beginTransaction()
        try {
            numInserted = values!!.size
            for (cv in values) {
                val newID = sqlDB.insert(table, null, cv)
                if (newID <= 0) {
                    numInserted = -1
                    break
                    //throw SQLException("Failed to insert row into $uri")
                }
            }
            sqlDB.setTransactionSuccessful()
            context!!.contentResolver.notifyChange(uri, null)
        } finally {
            sqlDB.endTransaction()
        }

        return numInserted
    }


    override fun insert(uri: Uri?, cv: ContentValues?): Uri {

        var table: String
        val uriType = sUriMatcher.match(uri)
        val sqlDB = myDB!!.writableDatabase

        when (uriType) {
            DATUM -> {
                table = DBHelper.TABLE_1
            }
            else -> throw IllegalArgumentException("Unknown URI")
        }
        val id = sqlDB.insert(table, null, cv)
        context.contentResolver.notifyChange(uri, null)
        return Uri.parse(table + "/" + id)
    }


    override fun update(p0: Uri?, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        throw IllegalArgumentException("Unknown URI")
    }

    override fun delete(uri: Uri?, where: String?, whereArgs: Array<out String>?): Int {
        val uriType = sUriMatcher.match(uri)
        val sqlDB = myDB!!.writableDatabase
        var selection = where
        var table: String
        when (uriType) {
            DATUM -> {
                table = DBHelper.TABLE_1
            }
            DATUM_ID -> {
                table = DBHelper.TABLE_1
                selection += DBHelper.COLUMN_ID + "=" + uri!!.lastPathSegment
            }
            else -> throw IllegalArgumentException("Unknown URI")
        }
        return sqlDB.delete(table, selection, whereArgs)
    }

    override fun getType(p0: Uri?): String {
        throw IllegalArgumentException("Unknown URI")
    }

}