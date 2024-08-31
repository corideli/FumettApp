package it.insubria.fumettapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileWriter

//si occupa della creazione, gestione e interazione con il database, consentendo di eseguire operazioni CRUD sui dati relativi ai fumetti
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    //estende `SQLiteOpenHelper`
    //il costruttore della classe richiede un `Context`, che rappresenta il contesto dell'applicazione

    companion object {//contiene le costanti che definiscono la struttura del database e i campi della tabella

        private const val DATABASE_NAME = "fumetti.db"
        private const val DATABASE_VERSION = 2 // Incrementa la versione del database

        const val TABLE_FUMETTI = "fumetti"
        const val COLUMN_ID = "id"
        const val COLUMN_TITOLO = "titolo"
        const val COLUMN_AUTORE = "autore"
        const val COLUMN_NUMERO_PAGINE = "numero_pagine"
        const val COLUMN_STATO = "stato"
        const val COLUMN_COLLANA = "collana"

        const val TABLE_COLLANE = "Collane" // Nome della tabella Collane
        const val COLUMN_NOME = "nome" // Colonna della tabella Collane

        private const val TABLE_CREATE_FUMETTI =
            "CREATE TABLE $TABLE_FUMETTI (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_TITOLO TEXT, " +
                    "$COLUMN_AUTORE TEXT, " +
                    "$COLUMN_NUMERO_PAGINE INTEGER, " +
                    "$COLUMN_STATO TEXT," +
                    "$COLUMN_COLLANA TEXT)"

        private const val TABLE_CREATE_COLLANE =
            "CREATE TABLE $TABLE_COLLANE (" +
                    "$COLUMN_NOME TEXT PRIMARY KEY)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE_FUMETTI)
        db.execSQL(TABLE_CREATE_COLLANE) // Creazione della tabella Collane
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(TABLE_CREATE_COLLANE) // Crea la tabella Collane se si aggiorna dalla versione 1
        }
    }

    // Funzioni CRUD (per interagire con il database)
    fun insertFumetto(fumetto: Fumetto): Long { //inserisce un nuovo fumetto nella tabella fumetti
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TITOLO, fumetto.titolo)
            put(COLUMN_AUTORE, fumetto.autore)
            put(COLUMN_NUMERO_PAGINE, fumetto.numeroPagine)
            put(COLUMN_STATO, fumetto.stato.name)
            put(COLUMN_COLLANA, fumetto.collana)
        }
        return db.insert(TABLE_FUMETTI, null, contentValues)
    } //restituisce l'ID della riga appena inserita.

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
                val stato = Stato.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATO)))
                val collana = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLLANA))
                val fumetto = Fumetto(id, titolo, autore, numeroPagine, stato, collana)
                fumetti.add(fumetto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return fumetti
    } //restituisce una lista di tutti i fumetti presenti nel database

    fun updateFumetto(fumetto: Fumetto): Int {//aggiorna i dati di un fumetto esistente nel database
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_TITOLO, fumetto.titolo)
            put(COLUMN_AUTORE, fumetto.autore)
            put(COLUMN_NUMERO_PAGINE, fumetto.numeroPagine)
            put(COLUMN_STATO, fumetto.stato.name)
            put(COLUMN_COLLANA, fumetto.collana)
        }
        return db.update(
            TABLE_FUMETTI,
            contentValues,
            "$COLUMN_ID = ?",
            arrayOf(fumetto.id.toString())
        )//restituisce il numero di righe aggiornate
    }

    fun getFumettoById(id: Long): Fumetto? {//restituisce un singolo fumetto in base all'ID specificato
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
            val collana = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLLANA))
            cursor.close()
            Fumetto(id, titolo, autore, numeroPagine, stato, collana)
        } else {
            cursor.close()
            null
        }
    }

    fun deleteFumetto(fumettoId: Long): Int {//elimina un fumetto dal database in base all'ID
        val db = writableDatabase
        return db.delete(TABLE_FUMETTI, "$COLUMN_ID=?", arrayOf(fumettoId.toString()))
    }//Restituisce il numero di righe eliminate

    //Funzioni di Ricerca e Filtraggio
    @SuppressLint("Range")
    fun searchFumetti(searchTerm: String): List<Fumetto> { //cerca fumetti nel database il cui titolo o autore corrisponde al termine di ricerca
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
            val collana = cursor.getString(cursor.getColumnIndex(COLUMN_COLLANA))
            val fumetto = Fumetto(id, titolo, autore, numeroPagine, stato, collana)
            fumetti.add(fumetto)
        }

        cursor.close()
        return fumetti
    }

    @SuppressLint("Range")
    fun getAllCollane(): List<String> {//recupera tutte le diverse "collane" presenti nel database, cioè tutte le serie o collezioni di fumetti
        val collane = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT DISTINCT collana FROM $TABLE_FUMETTI"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val collana = cursor.getString(cursor.getColumnIndex("collana"))
            collane.add(collana)
        }

        cursor.close()
        return collane
    }

    @SuppressLint("Range")
    fun getFumettiPerCollana(collana: String): List<Fumetto> {//recupera tutti i fumetti che appartengono a una determinata collana

        val fumetti = mutableListOf<Fumetto>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FUMETTI WHERE collana = ?"
        val cursor = db.rawQuery(query, arrayOf(collana))

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
            val titolo = cursor.getString(cursor.getColumnIndex(COLUMN_TITOLO))
            val autore = cursor.getString(cursor.getColumnIndex(COLUMN_AUTORE))
            val numeroPagine = cursor.getInt(cursor.getColumnIndex(COLUMN_NUMERO_PAGINE))
            val stato = cursor.getString(cursor.getColumnIndex(COLUMN_STATO))

            val fumetto = Fumetto(id, titolo, autore, numeroPagine, Stato.valueOf(stato), collana)
            fumetti.add(fumetto)
        }

        cursor.close()
        return fumetti
    }//restituisce una lista di fumetti filtrata per la collana specificata

    @SuppressLint("Range")
    fun getFumettiMancanti(): List<Fumetto> {//recupera tutti i fumetti che hanno lo stato "MANCANTE" nel database
        val fumetti = mutableListOf<Fumetto>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FUMETTI WHERE $COLUMN_STATO = ?"
        val cursor = db.rawQuery(query, arrayOf(Stato.MANCANTE.name))

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
            val titolo = cursor.getString(cursor.getColumnIndex(COLUMN_TITOLO))
            val autore = cursor.getString(cursor.getColumnIndex(COLUMN_AUTORE))
            val numeroPagine = cursor.getInt(cursor.getColumnIndex(COLUMN_NUMERO_PAGINE))
            val stato = Stato.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_STATO)))
            val collana = cursor.getString(cursor.getColumnIndex(COLUMN_COLLANA))
            val fumetto = Fumetto(id, titolo, autore, numeroPagine, stato, collana)
            fumetti.add(fumetto)
        }

        cursor.close()
        return fumetti
    }

    fun esisteFumettoConDettagli(titolo: String, autore: String, numeroPagine: Int, collana: String, idEscludere: Long? = null): Boolean {
        val db = this.readableDatabase
        val query = if (idEscludere != null) {
            "SELECT * FROM $TABLE_FUMETTI WHERE $COLUMN_TITOLO = ? AND $COLUMN_AUTORE = ? AND $COLUMN_NUMERO_PAGINE = ? AND $COLUMN_COLLANA = ? AND $COLUMN_ID != ?"
        } else {
            "SELECT * FROM $TABLE_FUMETTI WHERE $COLUMN_TITOLO = ? AND $COLUMN_AUTORE = ? AND $COLUMN_NUMERO_PAGINE = ? AND $COLUMN_COLLANA = ?"
        }
        val cursor = db.rawQuery(query, if (idEscludere != null) {
            arrayOf(titolo, autore, numeroPagine.toString(), collana, idEscludere.toString())
        } else {
            arrayOf(titolo, autore, numeroPagine.toString(), collana)
        })

        val esiste = cursor.count > 0
        cursor.close()
        return esiste
    }


    fun updateCollanaNome(oldName: String, newName: String): Boolean {
        val db = writableDatabase
        var successo = false

        db.beginTransaction()
        try {
            // 1. Aggiorna il nome nella tabella Collane
            val contentValues = ContentValues().apply {
                put(COLUMN_NOME, newName)
            }
            val resultCollane = db.update(
                TABLE_COLLANE,
                contentValues,
                "$COLUMN_NOME = ?",
                arrayOf(oldName)
            )
            Log.d("DatabaseHelper", "Update Collane Result: $resultCollane")

            // 2. Aggiorna il nome della collana nella tabella Fumetti
            val contentValuesFumetti = ContentValues().apply {
                put(COLUMN_COLLANA, newName)
            }
            val resultFumetti = db.update(
                TABLE_FUMETTI,
                contentValuesFumetti,
                "$COLUMN_COLLANA = ?",
                arrayOf(oldName)
            )
            Log.d("DatabaseHelper", "Update Fumetti Result: $resultFumetti")
            Log.d("DatabaseHelper", "Updating Collane from $oldName to $newName")

            if (resultCollane > 0 && resultFumetti > 0) {
                db.setTransactionSuccessful()
                successo = true
            }
        } finally {
            db.endTransaction()
        }

        return successo
    }

    fun aggiungiCollanaSeNecessario(nomeCollana: String) {
        val db = writableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_COLLANE WHERE $COLUMN_NOME = ?"
        val cursor = db.rawQuery(query, arrayOf(nomeCollana))

        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0)
            if (count == 0) {
                // La collana non esiste, quindi la aggiungiamo
                val contentValues = ContentValues().apply {
                    put(COLUMN_NOME, nomeCollana)
                }
                db.insert(TABLE_COLLANE, null, contentValues)
            }
        }
        cursor.close()
    }

    fun deleteCollanaEAssociati(collana: String) {
        writableDatabase.beginTransaction()
        try {
            writableDatabase.delete("Fumetti", "collana = ?", arrayOf(collana))
            writableDatabase.delete("Collane", "nome = ?", arrayOf(collana))
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
    }



    @SuppressLint("Range")
    fun createBackup(context: Context) {//crea un backup del database esportando i dati in un file CSV
        val db = this.readableDatabase

        // Estrai i dati
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FUMETTI", null)
        val backupFile = File(context.filesDir, "backup.csv")
        val writer = FileWriter(backupFile)

        writer.use {
            // Scrivi l'intestazione del CSV
            it.write("id,titolo,autore,numero_pagine,stato,collana\n")
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val titolo = cursor.getString(cursor.getColumnIndex(COLUMN_TITOLO))
                val autore = cursor.getString(cursor.getColumnIndex(COLUMN_AUTORE))
                val numeroPagine = cursor.getInt(cursor.getColumnIndex(COLUMN_NUMERO_PAGINE))
                val stato = Stato.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_STATO)))
                val collana = cursor.getString(cursor.getColumnIndex(COLUMN_COLLANA))
                val fumetto = Fumetto(id, titolo, autore, numeroPagine, stato, collana)
                it.write("$id,$titolo,$autore,$numeroPagine,$stato,$collana\n")
            }
        }
        cursor.close()
        db.close()
    }
}