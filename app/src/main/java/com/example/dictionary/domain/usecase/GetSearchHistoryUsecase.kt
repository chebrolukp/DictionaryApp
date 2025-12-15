package com.example.dictionary.domain.usecase

import com.example.dictionary.core.util.Resource
import com.example.dictionary.domain.repository.WordInfoRepository
import kotlinx.coroutines.flow.Flow

class GetSearchHistoryUseCase(
    private val repository: WordInfoRepository
) {
    operator fun invoke(): Flow<Resource<List<String>>> {
        return repository.getSearchHistory()
    }
}
