package com.example.dictionary.domain.usecase

import com.example.dictionary.domain.repository.WordInfoRepository
import javax.inject.Inject

class GetLocalWordInfoUsecase @Inject constructor(
    private val repository: WordInfoRepository
) {
    suspend operator fun invoke(word:String) = repository.getLocalWordInfo(word)
}
