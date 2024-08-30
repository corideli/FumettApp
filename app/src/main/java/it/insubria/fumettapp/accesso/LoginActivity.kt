package it.insubria.fumettapp.accesso

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.insubria.fumettapp.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Carica il LoginFragment all'avvio dell'attività solo se savedInstanceState è null
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }
}


