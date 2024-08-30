package it.insubria.fumettapp

//Classe per rappresentare un fumetto e le sua informazioni

data class Fumetto(
    val id: Long = 0,
    val titolo: String,
    val autore: String,
    val numeroPagine: Int,
    var stato: Stato,
    var collana: String
)

enum class Stato {
    PRESENTE,
    PRENOTAZIONE,
    MANCANTE
}
