package com.example.dictionary.data.remote.dto

import com.example.dictionary.domain.model.Phonetic
import kotlinx.serialization.Serializable

@Serializable
data class PhoneticDto(
    val text: String,
    val audio: String,
    val sourceUrl: String = "",
    val license: LicenseDto? = null
) {
    fun toPhonetic(): Phonetic {
        return Phonetic(
            text = text,
            audio = audio
        )
    }
}