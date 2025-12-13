package com.example.dictionary.domain.model

import com.example.dictionary.data.remote.dto.DefinitionDto
import kotlinx.serialization.Serializable

@Serializable
class Definition(
    val definition: String,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val example: String? = null
) {
    fun toDto(): DefinitionDto {
        return DefinitionDto(
            definition = definition,
            synonyms = synonyms,
            antonyms = antonyms,
            example = example
        )
    }
}