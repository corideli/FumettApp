package it.insubria.fumettapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "fumetti.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_FUMETTI = "fumetti"
        const val COLUMN_ID = "id"
        const val COLUMN_TITOLO = "titolo"
        const val COLUMN_AUTORE = "autore"
        const val COLUMN_NUMERO_PAGINE = "numero_pagine"
        const val COLUMN_STATO = "stato"

        private const val TABLE_CREATE =
            "CREATE TABLE $TABLE_FUMETTI (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_TITOLO TEXT, " +
                    "$COLUMN_AUTORE TEXT, " +
                    "$COLUMN_NUMERO_PAGINE INTEGER, " +
                    "$COLUMN_STATO TEXT)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FUMETTI")
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
        return db.insert(TABLE_FUMETTI, null, contentValues)
    }

    fun getAllFumetti(): List<Fumetto> {
        val fumetti = mutableListOf<Fumetto>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FUMETTI", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val titolo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITOLO))
                val autore = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTORE))
                val numeroPagine = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUMERO_PAGINE))
                val stato =
                    Stato.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATO)))
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
        return db.update(
            TABLE_FUMETTI,
            contentValues,
            "$COLUMN_ID = ?",
            arrayOf(fumetto.id.toString())
        )
    }

    fun getFumettoById(id: Long): Fumetto? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_FUMETTI,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
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

    fun deleteFumetto(fumettoId: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_FUMETTI, "$COLUMN_ID=?", arrayOf(fumettoId.toString()))
    }

    fun searchFumetti(searchTerm: String): List<Fumetto> {
        val fumetti = mutableListOf<Fumetto>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FUMETTI WHERE $COLUMN_TITOLO LIKE '%$searchTerm%' OR $COLUMN_AUTORE LIKE '%$searchTerm%'"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
            val titolo = cursor.getString(cursor.getColumnIndex(COLUMN_TITOLO))
            val autore = cursor.getString(cursor.getColumnIndex(COLUMN_AUTORE))
            val numeroPagine = cursor.getInt(cursor.getColumnIndex(COLUMN_NUMERO_PAGINE))
            val stato = Stato.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_STATO)))

            val fumetto = Fumetto(id, titolo, autore, numeroPagine, stato)
            fumetti.add(fumetto)
        }

        cursor.close()
        return fumetti
    }


    /*fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
    }*/
}

