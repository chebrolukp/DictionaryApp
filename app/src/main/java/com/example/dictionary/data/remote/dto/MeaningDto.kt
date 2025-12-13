package com.example.dictionary.data.remote.dto

import com.example.dictionary.domain.model.Meaning
import kotlinx.serialization.Serializable

@Serializable
data class MeaningDto(
    val partOfSpeech: String,
    val definitions: List<DefinitionDto>,
    val synonyms: List<String>,
    val antonyms: List<String>
) {
    fun toMeaning(): Meaning {
        return Meaning(
            partOfSpeech = partOfSpeech,
            definitions = definitions.map { it.toDefinition() },
            synonyms = synonyms,
            antonyms = antonyms
        )
    }
}