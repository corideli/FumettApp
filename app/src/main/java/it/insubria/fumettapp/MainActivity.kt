package it.insubria.fumettapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import it.insubria.fumettapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: ComicDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.fragment1Btn.setOnClickListener{
            replaceFragment()
        }

        binding.fragment2Btn.setOnClickListener{
            replaceFragment()
        }*/

        dbHelper = ComicDatabaseHelper(this)

        // Esempio di aggiunta di un fumetto
        val newComic =
            Comic(title = "Batman", author = "Bob Kane", year = 1939, genre = "Superhero")
        dbHelper.addComic(newComic)

        // Esempio di recupero di tutti i fumetti
        val comics = dbHelper.getAllComics()
        comics.forEach {
            println("Comic: ${it.title}, Author: ${it.author}")

            enableEdgeToEdge()
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}
    /*private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beingTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }*/