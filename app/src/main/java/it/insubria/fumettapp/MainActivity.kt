package it.insubria.fumettapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import it.insubria.fumettapp.DatabaseHelper.Companion.TABLE_FUMETTI
import it.insubria.fumettapp.DatabaseHelper.Companion.COLUMN_ID
import it.insubria.fumettapp.DatabaseHelper.Companion.COLUMN_TITOLO
import it.insubria.fumettapp.DatabaseHelper.Companion.COLUMN_AUTORE
import it.insubria.fumettapp.DatabaseHelper.Companion.COLUMN_STATO
import it.insubria.fumettapp.DatabaseHelper.Companion.COLUMN_COLLANA
import it.insubria.fumettapp.DatabaseHelper.Companion.COLUMN_NUMERO_PAGINE
import it.insubria.fumettapp.accesso.LoginActivity
import it.insubria.fumettapp.accesso.Logout

import it.insubria.fumettapp.databinding.ActivityMainBinding
//gestisce la navigazione principale dell'app
class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_CREATE_FILE = 1
    }//definisce una costante utilizzata per identificare la richiesta di creazione di un file durante il backup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this) //creata istanza di DatabaseHelper per gestire le operazioni sul database locale
        // Creazione di un backup
        dbHelper.createBackup(this)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Rimani sulla Home, quindi non fare nulla
                    true
                }

                R.id.nav_logout -> {
                    Logout(this).signOutUser()
                    true
                }

                R.id.nav_add -> {
                    val intent = Intent(this, AggiungiFumettoActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        val btnLibreria: Button = findViewById(R.id.btnLibreria)//apre `LibraryActivity`, dove l'utente può vedere la libreria dei fumetti
        val btnCerca: Button = findViewById(R.id.btnCerca)//apre `CercaActivity`, dove l'utente può cercare fumetti
        val btnDesideri: Button = findViewById(R.id.btnDesideri)//apre `DesideriActivity`, che mostra una lista dei fumetti mancanti
        val backupButton: Button = findViewById(R.id.backupButton)//apre un'intent per creare un nuovo documento, usato per salvare il backup dei dati

        btnLibreria.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        btnCerca.setOnClickListener {
            val intent = Intent(this, CercaActivity::class.java)
            startActivity(intent)
        }

        btnDesideri.setOnClickListener {
            val intent = Intent(this, DesideriActivity::class.java)
            startActivity(intent)
        }
        backupButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/csv"
                putExtra(Intent.EXTRA_TITLE, "backup.csv")
            }
            startActivityForResult(intent, REQUEST_CODE_CREATE_FILE)
        }

    }

    override fun onResume() {//imposta `nav_home` come l'elemento selezionato quando l'activity torna in primo piano
        super.onResume()
        // Assicurati che l'item corretto sia selezionato
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CREATE_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                saveBackupToUri(uri)
            }
        }
    }//se l'utente ha creato un file per il backup, viene richiamato il metodo `saveBackupToUri` per scrivere i dati del database nel file CSV

    private fun saveBackupToUri(uri: Uri) {//esegue un backup dei dati contenuti nella tabella `TABLE_FUMETTI` del database, scrivendoli nel file selezionato dall'utente
        val db = DatabaseHelper(this).readableDatabase//database aperto in modalità di sola lettura
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FUMETTI", null)//eseguita una query per recuperare tutti i record dalla tabella dei fumetti

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = outputStream.bufferedWriter()

                writer.use {//dati estratti dal cursor e scritti nel file CSV, seguendo l'ordine specificato
                    it.write("id,titolo,autore,numero_pagine,stato,collana\n")
                    while (cursor.moveToNext()) {
                        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                        val titolo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITOLO))
                        val autore = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTORE))
                        val numeroPagine = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUMERO_PAGINE))
                        val stato = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATO))
                        val collana = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLLANA))
                        it.write("$id,$titolo,$autore,$numeroPagine,$stato,$collana\n")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }//cursor e database vengono chiusi dopo l'operazione
    }
}