package com.example.dictionary.data.remote.dto

import com.example.dictionary.domain.model.Definition
import kotlinx.serialization.Serializable

@Serializable
data class DefinitionDto(
    val definition: String,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val example: String? = null
) {
    fun toDefinition(): Definition{
        return Definition(
            definition = definition,
            synonyms = synonyms,
            antonyms = antonyms,
            example = example
        )
    }
}