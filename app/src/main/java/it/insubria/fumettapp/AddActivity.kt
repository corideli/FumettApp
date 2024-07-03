/*package it.insubria.fumettapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddActivity : AppCompatActivity() {

    private lateinit var dbHelper: ComicDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = ComicDatabaseHelper(this)

        // Esempio di aggiunta di un fumetto
        val newComic =
            Comic(title = "Batman", author = "Bob Kane", year = 1939, genre = "Superhero", pages = 182)
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
}*/
package it.insubria.fumettapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddActivity : AppCompatActivity() {
    private lateinit var titleInput: EditText
    private lateinit var authorInput: EditText
    private lateinit var pagesInput: EditText
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        titleInput = findViewById(R.id.title_input)
        authorInput = findViewById(R.id.author_input)
        pagesInput = findViewById(R.id.pages_input)
        addButton = findViewById(R.id.add_button)
        addButton.setOnClickListener(View.OnClickListener {
            val myDB = MyDatabaseHelper(this@AddActivity)
            myDB.addBook(
                titleInput.text.toString().trim(),
                authorInput.text.toString().trim(),
                pagesInput.text.toString().trim().toInt()
            )
        })
    }
}
