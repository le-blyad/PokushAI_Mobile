package com.example.pokushai_mobile

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.BufferedReader
import java.io.InputStreamReader

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = """
            CREATE TABLE users (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT,
                image BLOB,
                number TEXT UNIQUE,
                email TEXT UNIQUE
            );
        """.trimIndent()
        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        loadDatabaseFromFile(db)
    }

    private fun loadDatabaseFromFile(db: SQLiteDatabase) {
        try {
            val inputStream = context.assets.open("users.sql")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sql = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                sql.append(line)
                sql.append("\n")
            }

            reader.close()

            db.execSQL(sql.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
