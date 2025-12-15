package com.example.dictionary.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dictionary.R
import com.example.dictionary.domain.model.Definition
import com.example.dictionary.domain.model.Meaning
import com.example.dictionary.domain.model.Phonetic
import com.example.dictionary.domain.model.WordInfo
import com.example.dictionary.presentation.preview.MultiPreview

@Composable
fun WordInfoItem(
    wordInfo: WordInfo,
    modifier: Modifier = Modifier,
    onPlayAudio: (String) -> Unit = {}
) {
    Card(
        modifier = modifier.background(
            colorResource(R.color.half_white),
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Column(modifier = modifier) {
            Text(
                text = wordInfo.word,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            wordInfo.phonetic?.let {
                Text(text = it, fontWeight = FontWeight.Light)
                Spacer(modifier = Modifier.height(8.dp))
            }
            val phonetic = wordInfo.phonetics?.first()
            phonetic?.let {
                Row(Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically){
                    Text(text = phonetic.text)
                    if (phonetic.audio.isNotBlank()) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = stringResource(R.string.label_play_audio),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { onPlayAudio(phonetic.audio) }
                        )
                    }
                }
            }
            wordInfo.meanings?.forEach { meaning ->
                Text(text = meaning.partOfSpeech, fontWeight = FontWeight.Bold)
                meaning.definitions.forEachIndexed { i, definition ->
                    Text(text = "${i + 1}. ${definition.definition}")
                    Spacer(modifier = Modifier.height(8.dp))
                    definition.example?.let { example ->
                        Text(text = stringResource(R.string.label_example, example))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@MultiPreview
@Composable
fun WordInfoItemPreview() {
    WordInfoItem(
        wordInfo = WordInfo(
            word = "bank",
            phonetic = "bänk",
            phonetics = listOf(
                Phonetic(
                    text = "/bæŋk/",
                    audio = "https://api.dictionaryapi.dev/media/pronunciations/en/bank-us.mp3"
                )
            ),
            sourceUrls = listOf("url1", "url2"),
            meanings = listOf(
                Meaning(
                    partOfSpeech = "noun",
                    definitions = listOf(
                        Definition(
                            definition = "a financial institution",
                            example = "",
                            synonyms = emptyList(),
                            antonyms = emptyList()
                        ),
                        Definition(
                            definition = "a place where money is held",
                            example = "",
                            synonyms = emptyList(),
                            antonyms = emptyList()
                        )
                    ),
                    synonyms = emptyList(),
                    antonyms = emptyList(),
                )
            )
        ),
    )
}