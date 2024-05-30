package it.insubria.fumettapp

//Classe per rappresentare un fumetto.

data class Comic(
    val id: Long = 0,
    val title: String,
    val author: String,
    val year: Int,
    val genre: String
)
