package it.insubria.fumettapp

//Classe per rappresentare un fumetto.

data class Fumetto(
    val id: Long = 0,
    val titolo: String,
    val autore: String,
    val numeroPagine: Int,
    var stato: Stato
)

enum class Stato {
    PRESENTE,
    PRENOTAZIONE,
    MANCANTE
}
