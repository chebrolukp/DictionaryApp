package com.example.dictionary.presentation.navigation

sealed class Screen(val route: String) {
    data object Home: Screen("home")
    data object History : Screen("history")
    data object WordDetails : Screen("word_details/{word}") {
        fun create(word: String) = "word_details/$word"
    }
}

sealed interface Destination {
    data object Home : Destination
    data object History : Destination
    data class WordDetails(val word: String) : Destination
}