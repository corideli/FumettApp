package it.insubria.fumettapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import android.content.ContentValues
import android.database.Cursor

//Classe che gestirà la creazione e l'aggiornamento del database.

class ComicDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "comicLibrary.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_COMICS = "comics"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_AUTHOR = "author"
        private const val COLUMN_YEAR = "year"
        private const val COLUMN_GENRE = "genre"

        private const val TABLE_CREATE =
            "CREATE TABLE $TABLE_COMICS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_TITLE TEXT NOT NULL, " +
                    "$COLUMN_AUTHOR TEXT NOT NULL, " +
                    "$COLUMN_YEAR INTEGER NOT NULL, " +
                    "$COLUMN_GENRE TEXT NOT NULL);"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMICS")
        onCreate(db)
    }

//Metodi per le operazioni Create, Read, Update, Delete nel database helper.

    fun addComic(comic: Comic): Long {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, comic.title)
            put(COLUMN_AUTHOR, comic.author)
            put(COLUMN_YEAR, comic.year)
            put(COLUMN_GENRE, comic.genre)
        }

        val db = this.writableDatabase
        val result = db.insert(TABLE_COMICS, null, values)
        db.close()
        return result
    }

    fun getAllComics(): List<Comic> {
        val comics = mutableListOf<Comic>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_COMICS", null)

        if (cursor.moveToFirst()) {
            do {
                val comic = Comic(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)),
                    genre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENRE))
                )
                comics.add(comic)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return comics
    }

    fun updateComic(comic: Comic): Int {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, comic.title)
            put(COLUMN_AUTHOR, comic.author)
            put(COLUMN_YEAR, comic.year)
            put(COLUMN_GENRE, comic.genre)
        }

        val db = this.writableDatabase
        val result = db.update(TABLE_COMICS, values, "$COLUMN_ID = ?", arrayOf(comic.id.toString()))
        db.close()
        return result
    }

    fun deleteComic(id: Long): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_COMICS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }
}