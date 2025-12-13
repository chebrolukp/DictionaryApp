package com.example.dictionary.data.remote.dto

import com.example.dictionary.data.local.entity.WordInfoEntity
import kotlinx.serialization.Serializable

@Serializable
data class WordInfoDto(
    val word: String,
    val phonetic: String? = null,
    val phonetics: List<PhoneticDto>? = emptyList(),
    val meanings: List<MeaningDto>? = emptyList(),
    val license: LicenseDto? = null,
    val sourceUrls: List<String>? = emptyList()
) {
    fun toWordInfoEntity(): WordInfoEntity{
        return WordInfoEntity(
            word = word,
            phonetic = phonetic,
            phonetics = phonetics?.map { it.toPhonetic() },
            meanings = meanings?.map { it.toMeaning() },
            license = license?.toLicense(),
            sourceUrls = sourceUrls
        )
    }
}