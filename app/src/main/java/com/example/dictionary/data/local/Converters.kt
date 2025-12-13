package com.example.dictionary.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.dictionary.domain.model.Definition
import com.example.dictionary.domain.model.License
import com.example.dictionary.domain.model.Meaning
import com.example.dictionary.domain.model.Phonetic
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class Converters(private val jsonParser: Json) {

    @TypeConverter
    fun fromMeaningsJson(json: String): List<Meaning>{
        return jsonParser.decodeFromString(json)
    }

    @TypeConverter
    fun toMeaningsJson(meanings: List<Meaning>): String{
        return jsonParser.encodeToString(meanings)
    }

    @TypeConverter
    fun fromPhoneticsJson(json: String): List<Phonetic>{
        return jsonParser.decodeFromString(json)
    }

    @TypeConverter
    fun toPhoneticsJson(phonetics: List<Phonetic>): String{
        return jsonParser.encodeToString(phonetics)
    }

    @TypeConverter
    fun fromDefinitionJson(json: String): List<Definition>{
        return jsonParser.decodeFromString(json)
    }

    @TypeConverter
    fun toDefinitionJson(definitions: List<Definition>): String{
        return jsonParser.encodeToString(definitions)
    }

    @TypeConverter
    fun fromLicenseJson(json:String): License{
        return jsonParser.decodeFromString(json)
    }

    @TypeConverter
    fun toLicenseJson(license: License): String{
        return jsonParser.encodeToString(license)
    }

    @TypeConverter
    fun fromStringList(json:String): List<String>{
        return jsonParser.decodeFromString(json)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String{
        return jsonParser.encodeToString(list)
    }
}