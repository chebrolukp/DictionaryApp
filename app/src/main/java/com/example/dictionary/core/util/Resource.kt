package com.example.dictionary.core.util

import java.lang.Exception

sealed class Resource<T>(val data:T? = null, val exception: DictionaryException? = null) {
    class Loading<T>(data: T? = null): Resource<T>(data)
    class Error<T>(data: T? = null, exception: DictionaryException): Resource<T>(data, exception)
    class Success<T>(data: T): Resource<T>(data)
}