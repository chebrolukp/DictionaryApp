package com.example.dictionary.domain.repository

import com.example.dictionary.core.util.Resource
import com.example.dictionary.domain.model.WordInfo
import kotlinx.coroutines.flow.Flow

interface WordInfoRepository {
    fun getWordInfo(word: String): Flow<Resource<List<WordInfo>>>
    fun getSearchHistory(): Flow<Resource<List<String>>>
}