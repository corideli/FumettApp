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

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    companion object {
        const val REQUEST_CODE_CREATE_FILE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val dbHelper = DatabaseHelper(this)
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

        val btnLibreria: Button = findViewById(R.id.btnLibreria)
        val btnCerca: Button = findViewById(R.id.btnCerca)
        val btnDesideri: Button = findViewById(R.id.btnDesideri)
        val backupButton: Button = findViewById(R.id.backupButton)


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

    override fun onResume() {
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
    }
    private fun saveBackupToUri(uri: Uri) {
        val db = DatabaseHelper(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FUMETTI", null)

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = outputStream.bufferedWriter()

                writer.use {
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
        }
    }
}