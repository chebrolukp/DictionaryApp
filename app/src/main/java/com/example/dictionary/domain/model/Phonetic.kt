package com.example.dictionary.domain.model

import com.example.dictionary.data.remote.dto.PhoneticDto
import kotlinx.serialization.Serializable

@Serializable
class Phonetic(
    val text: String,
    val audio: String
) {
    fun toDto(): PhoneticDto{
        return PhoneticDto(
            text = text,
            audio = audio
        )
    }
}