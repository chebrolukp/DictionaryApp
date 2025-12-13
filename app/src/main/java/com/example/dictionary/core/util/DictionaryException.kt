package com.example.dictionary.core.util

sealed interface DictionaryException {
    data object NetworkException : DictionaryException
    data object ServerException : DictionaryException
    data object DatabaseException : DictionaryException
    data object UnknownException : DictionaryException
    data object NoInternetException : DictionaryException
    data object WordNotFoundException : DictionaryException
}
