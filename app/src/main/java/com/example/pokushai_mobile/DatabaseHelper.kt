package com.example.pokushai_mobile

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NUMBERS = "number"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_IMAGE = "image"

        private const val TABLE_CREATE = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_IMAGE BLOB,
                $COLUMN_NUMBERS TEXT UNIQUE,
                $COLUMN_EMAIL TEXT UNIQUE
            );
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $COLUMN_IMAGE BLOB")
        }
    }
}
