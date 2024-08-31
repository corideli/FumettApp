package it.insubria.fumettapp.accesso

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.insubria.fumettapp.R
//questa activity serve principalmente come contenitore per il `LoginFragment. Gestisce la schermata di login
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//assicura che l'attività venga correttamente inizializzata
        setContentView(R.layout.activity_login)//viene impostato il layout per l'activity

        // Carica il LoginFragment all'avvio dell'attività solo se savedInstanceState è null
        //se è `null`, significa che l'activity è stata appena creata per la prima volta
        if (savedInstanceState == null) {//esegue una transazione del `FragmentManager` per sostituire il contenuto del `fragment_container` con un'istanza di `LoginFragment`
            supportFragmentManager.beginTransaction()//ottiene un `FragmentTransaction` dal `FragmentManager` per gestire le operazioni sui frammenti
                .replace(R.id.fragment_container, LoginFragment())//sostituisce qualsiasi contenuto attuale del contenitore `R.id.fragment_container` con una nuova istanza di `LoginFragment`
                .commit()//finalizza la transazione, applicando la sostituzione del frammento. Visualizza il `LoginFragment` nell'interfaccia dell'activity
        }
    }
}


