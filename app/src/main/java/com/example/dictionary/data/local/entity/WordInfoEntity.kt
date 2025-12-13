package com.example.dictionary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dictionary.domain.model.License
import com.example.dictionary.domain.model.Meaning
import com.example.dictionary.domain.model.Phonetic
import com.example.dictionary.domain.model.WordInfo

@Entity
data class WordInfoEntity(
    val word: String,
    val phonetic: String? = null,
    val phonetics: List<Phonetic>? = null,
    val meanings: List<Meaning>? = null,
    val license: License? = null,
    val sourceUrls: List<String>? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) {
    fun toWordInfo(): WordInfo {
        return WordInfo(
            word = word,
            phonetic = phonetic,
            phonetics = phonetics,
            meanings = meanings,
            license = license,
            sourceUrls = sourceUrls
        )
    }
}