package com.example.dictionary.domain.model

import com.example.dictionary.data.remote.dto.MeaningDto
import kotlinx.serialization.Serializable

@Serializable
class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>,
    val synonyms: List<String>,
    val antonyms: List<String>
) {
    fun toDto(): MeaningDto{
        return MeaningDto(
            partOfSpeech = partOfSpeech,
            definitions = definitions.map { it.toDto() },
            synonyms = synonyms,
            antonyms = antonyms
        )
    }
}