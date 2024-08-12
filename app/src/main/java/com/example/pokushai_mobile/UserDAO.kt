package com.example.pokushai_mobile

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.content.SharedPreferences

class UserDAO(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    private val database: SQLiteDatabase = dbHelper.writableDatabase
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun addUser(username: String, password: String, number: String, email: String): Long {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("number", number)
            put("email", email)
        }
        return database.insert("users", null, values)
    }

    fun checkUser(inputFieldLogin: String, inputFieldPassword: String): Boolean {
        val columns = arrayOf("username")
        val selection = "username = ? AND password = ?"
        val selectionArgs = arrayOf(inputFieldLogin, inputFieldPassword)

        val cursor: Cursor = database.query("users", columns, selection, selectionArgs, null, null, null)
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
}
