package com.example.pokushai_mobile

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.content.SharedPreferences

class UserDAO(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    private val database: SQLiteDatabase = dbHelper.writableDatabase
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        const val TABLE_USERS = "users"
        const val COLUMN_IMAGE = "image"
        const val COLUMN_ID = "_id"
    }

    fun addUser(username: String, password: String, number: String, email: String): Long {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("number", number)
            put("email", email)
        }
        return database.insert(TABLE_USERS, null, values)
    }

    fun updateProfileImage(userId: Long, image: ByteArray?): Int {
        val values = ContentValues().apply {
            put(COLUMN_IMAGE, image)
        }
        return database.update(
            TABLE_USERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
    }


    fun checkUser(inputFieldLogin: String, inputFieldPassword: String): Boolean {
        val columns = arrayOf("username")
        val selection = "username = ? AND password = ?"
        val selectionArgs = arrayOf(inputFieldLogin, inputFieldPassword)

        val cursor: Cursor = database.query(
            TABLE_USERS, columns, selection, selectionArgs, null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun setUserLoggedIn(username: String) {
        with(sharedPreferences.edit()) {
            putString("logged_in_user", username)
            apply()
        }
    }

    fun getLoggedInUser(): String? {
        return sharedPreferences.getString("logged_in_user", null)
    }

    fun clearUserLoggedIn() {
        with(sharedPreferences.edit()) {
            remove("logged_in_user")
            apply()
        }
    }

    fun getProfileImage(userId: Long): ByteArray? {
        var cursor: Cursor? = null
        return try {
            cursor = database.query(
                TABLE_USERS,
                arrayOf(COLUMN_IMAGE),
                "$COLUMN_ID = ?",
                arrayOf(userId.toString()),
                null,
                null,
                null,
                "1"
            )

            if (cursor.moveToFirst()) {
                cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
            } else {
                null
            }
        } finally {
            cursor?.close()
        }
    }

    fun removeProfileImage(userId: Long): Int {
        val values = ContentValues().apply {
            put(COLUMN_IMAGE, null as ByteArray?)
        }
        return database.update(
            TABLE_USERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(userId.toString())
        )
    }

    fun getUserIdByUsername(username: String?): Long? {
        val cursor: Cursor? = database.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "username = ?",
            arrayOf(username),
            null,
            null,
            null,
            "1"
        )

        return try {
            if (cursor != null && cursor.moveToFirst()) {
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            } else {
                null
            }
        } finally {
            cursor?.close()
        }
    }

}
