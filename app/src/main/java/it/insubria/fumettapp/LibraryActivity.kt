package it.insubria.fumettapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LibraryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var emptyImageView: ImageView
    private lateinit var noDataTextView: TextView

    private lateinit var myDB: MyDatabaseHelper
    private lateinit var bookId: ArrayList<String>
    private lateinit var bookTitle: ArrayList<String>
    private lateinit var bookAuthor: ArrayList<String>
    private lateinit var bookPages: ArrayList<String>
    private lateinit var customAdapter: CustomAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        recyclerView = findViewById(R.id.recyclerView)
        addButton = findViewById(R.id.add_button)
        emptyImageView = findViewById(R.id.empty_imageview)
        noDataTextView = findViewById(R.id.no_data)

        addButton.setOnClickListener {
            val intent = Intent(this@LibraryActivity, AddActivity::class.java)
            startActivity(intent)
        }

        myDB = MyDatabaseHelper(this@LibraryActivity)
        bookId = ArrayList()
        bookTitle = ArrayList()
        bookAuthor = ArrayList()
        bookPages = ArrayList()

        storeDataInArrays()

        customAdapter = CustomAdapter(this@LibraryActivity, this, bookId, bookTitle, bookAuthor, bookPages)
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@LibraryActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            recreate()
        }
    }

    private fun storeDataInArrays() {
        val cursor: Cursor? = myDB.readAllData()
        if (cursor != null) {
            if (cursor.count == 0) {
                emptyImageView.visibility = View.VISIBLE
                noDataTextView.visibility = View.VISIBLE
            } else {
                while (cursor.moveToNext()) {
                    bookId.add(cursor.getString(0))
                    bookTitle.add(cursor.getString(1))
                    bookAuthor.add(cursor.getString(2))
                    bookPages.add(cursor.getString(3))
                }
                emptyImageView.visibility = View.GONE
                noDataTextView.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all) {
            confirmDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete All?")
        builder.setMessage("Are you sure you want to delete all Data?")
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            val myDB = MyDatabaseHelper(this@LibraryActivity)
            myDB.deleteAllData()
            // Refresh Activity
            val intent = Intent(this@LibraryActivity, LibraryActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        builder.create().show()
    }
}