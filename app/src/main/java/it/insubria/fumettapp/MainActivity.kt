package it.insubria.fumettapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import it.insubria.fumettapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLibreria.setOnClickListener{
            val intent = Intent(this , LibraryActivity::class.java)
            startActivity(intent)

        }

        /*binding.fragment1Btn.setOnClickListener{
            replaceFragment()
        }

        binding.fragment2Btn.setOnClickListener{
            replaceFragment()
        }*/
    }
}

    /*private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beingTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }*/