package com.example.dictionary.data.remote.dto

import com.example.dictionary.domain.model.License
import kotlinx.serialization.Serializable

@Serializable
data class LicenseDto(
    val name: String,
    val url: String
) {
    fun toLicense(): License {
        return License(
            name = name,
            url = url
        )
    }
}