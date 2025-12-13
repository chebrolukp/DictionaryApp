package com.example.dictionary.domain.model

import com.example.dictionary.data.remote.dto.WordInfoDto
import kotlinx.serialization.Serializable

@Serializable
data class WordInfo(
    val word: String,
    val phonetic: String? = null,
    val phonetics: List<Phonetic>? = null,
    val meanings: List<Meaning>? = null,
    val license: License? = null,
    val sourceUrls: List<String>? = null
) {
    fun toDto(): WordInfoDto {
        return WordInfoDto(
            word = word,
            phonetic = phonetic,
            phonetics = phonetics?.map { it.toDto() },
            meanings = meanings?.map { it.toDto() },
            license = license?.toDto(),
            sourceUrls = sourceUrls
        )
    }
}