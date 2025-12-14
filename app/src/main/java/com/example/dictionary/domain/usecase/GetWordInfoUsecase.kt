package com.example.dictionary.domain.usecase

import com.example.dictionary.core.util.Resource
import com.example.dictionary.domain.model.WordInfo
import com.example.dictionary.domain.repository.WordInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetWordInfoUsecase(
    private val repository: WordInfoRepository
) {
    operator fun invoke(word:String): Flow<Resource<List<WordInfo>>>{
        val trimmedWord = word.trim()
        if(trimmedWord.isBlank()){
            return flow{}
        }

        return repository.getWordInfo(trimmedWord)
    }
}