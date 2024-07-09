package it.insubria.fumettapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Rimani sulla Home, quindi non fare nulla
                    true
                }
                R.id.nav_favorites -> {
                    val intent = Intent(this, PreferitiActivity::class.java)
                    startActivity(intent)
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
    }

    override fun onResume() {
        super.onResume()
        // Assicurati che l'item corretto sia selezionato
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
    }
}