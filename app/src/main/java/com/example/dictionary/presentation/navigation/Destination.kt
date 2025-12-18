package com.example.dictionary.presentation.navigation

sealed interface Destination {
    data object Home: Destination
    data object History : Destination
}