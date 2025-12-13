package com.example.dictionary.domain.model

import com.example.dictionary.data.remote.dto.LicenseDto
import kotlinx.serialization.Serializable

@Serializable
class License(
    val name: String,
    val url: String
) {
    fun toDto(): LicenseDto{
        return LicenseDto(
            name = name,
            url = url
        )
    }
}