package it.insubria.fumettapp
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val context: Context? = context

    companion object {
        private const val DATABASE_NAME = "fumetti.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "fumetti"
        const val COLUMN_ID = "id"
        const val COLUMN_TITOLO = "titolo"
        const val COLUMN_AUTORE = "autore"
        const val COLUMN_NUMERO_PAGINE = "numero_pagine"
        const val COLUMN_STATO = "stato"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_TITOLO TEXT, "
                + "$COLUMN_AUTORE TEXT, "
                + "$COLUMN_NUMERO_PAGINE INTEGER, "
                + "$COLUMN_STATO TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Funzioni CRUD
    fun insertFumetto(fumetto: Fumetto): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TITOLO, fumetto.titolo)
            put(COLUMN_AUTORE, fumetto.autore)
            put(COLUMN_NUMERO_PAGINE, fumetto.numeroPagine)
            put(COLUMN_STATO, fumetto.stato.name)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }

    fun getAllFumetti(): List<Fumetto> {
        val fumetti = mutableListOf<Fumetto>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val titolo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITOLO))
                val autore = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTORE))
                val numeroPagine = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUMERO_PAGINE))
                val stato = Stato.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATO)))
                val fumetto = Fumetto(id, titolo, autore, numeroPagine, stato)
                fumetti.add(fumetto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return fumetti
    }

    //CORREGGERE I SEGUENTI METODI PER LA MODIFICA DEI FUMETTI ED ELIMINAZIONE

    fun updateFumetto(fumetto: Fumetto): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TITOLO, fumetto.titolo)
            put(COLUMN_AUTORE, fumetto.autore)
            put(COLUMN_NUMERO_PAGINE, fumetto.numeroPagine)
            put(COLUMN_STATO, fumetto.stato.name)
        }
        return db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(fumetto.id.toString()))
    }

    fun getFumettoById(id: Long): Fumetto? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, null, "$COLUMN_ID = ?", arrayOf(id.toString()), null, null, null)
        return if (cursor.moveToFirst()) {
            val titolo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITOLO))
            val autore = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTORE))
            val numeroPagine = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUMERO_PAGINE))
            val stato = Stato.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATO)))
            cursor.close()
            Fumetto(id, titolo, autore, numeroPagine, stato)
        } else {
            cursor.close()
            null
        }
    }


    /*fun deleteOneRow(row_id: String) {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(row_id))
        if (result == -1) {
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show()
        }
    }


    fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
    }*/
}

