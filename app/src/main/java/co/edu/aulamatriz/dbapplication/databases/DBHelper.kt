package co.edu.aulamatriz.dbapplication.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context?)
    : SQLiteOpenHelper(context, NAME_DB, null, VERSION_DB) {

    lateinit var _db: SQLiteDatabase

    init {
        _db = writableDatabase
    }

    companion object {
        val VERSION_DB = 1
        val NAME_DB = "AULA_DB"
        val TABLE_1 = "Joke"
        val COLUMN_ID = "_id"
        val COLUMN_SERVER_ID = "id"
        val COLUMN_JOKE = "joke"
        val COLUMN_CATEGORIES = "categories"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = """CREATE TABLE $TABLE_1 (
            | $COLUMN_ID integer primary key autoincrement,
            | $COLUMN_SERVER_ID integer,
            | $COLUMN_JOKE text,
            | $COLUMN_CATEGORIES text
            |)""".trimMargin()
        db!!.execSQL(query)
        Log.d("DBHelper", "TABLE CREATED $TABLE_1");
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

        val query = """DROP TABLE IF EXISTS $TABLE_1"""
        db!!.execSQL(query)

        onCreate(db)
    }
}