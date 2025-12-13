package com.example.dictionary.presentation

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dictionary.domain.model.Definition
import com.example.dictionary.domain.model.License
import com.example.dictionary.domain.model.Meaning
import com.example.dictionary.domain.model.Phonetic
import com.example.dictionary.domain.model.WordInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordInfoItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun sampleWord(): WordInfo {
        return WordInfo(
            word = "cat",
            phonetic = "/kat/",
            phonetics = listOf(
                Phonetic(
                    text = "/kat/",
                    audio = "https://api.dictionaryapi.dev/media/pronunciations/en/cat"
                )
            ),
            license = License(
                name = "name",
                url = "url"
            ),
            sourceUrls = listOf(
                "https://en.wikipedia.org/wiki/Cat"
            ),
            meanings = listOf(
                Meaning(
                    partOfSpeech = "noun",
                    definitions = listOf(
                        Definition(
                            definition = "A small domesticated animal.",
                            example = "The cat slept all day.",
                            synonyms = listOf(),
                            antonyms = listOf()
                        ),
                        Definition(
                            definition = "A wild feline species.",
                            example = null,
                            synonyms = listOf(),
                            antonyms = listOf()
                        )
                    ),
                    synonyms = listOf(),
                    antonyms = listOf()
                ),
                Meaning(
                    partOfSpeech = "verb",
                    definitions = listOf(
                        Definition(
                            definition = "To vomit.",
                            example = null,
                            synonyms = listOf(),
                            antonyms = listOf()
                        )
                    ),
                    synonyms = listOf(),
                    antonyms = listOf()
                )
            )
        )
    }

    @Test
    fun word_is_displayed() {
        val data = sampleWord()

        composeRule.setContent {
            WordInfoItem(wordInfo = data)
        }

        composeRule.onAllNodesWithText("cat").assertCountEquals(2)
    }

    @Test
    fun phonetic_is_displayed() {
        val data = sampleWord()

        composeRule.setContent {
            WordInfoItem(wordInfo = data)
        }

        composeRule.onNodeWithText("/kat/").assertIsDisplayed()
    }

    @Test
    fun meanings_parts_of_speech_are_shown() {
        val data = sampleWord()

        composeRule.setContent {
            WordInfoItem(wordInfo = data)
        }

        composeRule.onNodeWithText("noun").assertIsDisplayed()
        composeRule.onNodeWithText("verb").assertIsDisplayed()
    }

    @Test
    fun definitions_are_displayed_with_index() {
        val data = sampleWord()

        composeRule.setContent {
            WordInfoItem(wordInfo = data)
        }

        composeRule.onNodeWithText("1. A small domesticated animal.").assertIsDisplayed()
        composeRule.onNodeWithText("2. A wild feline species.").assertIsDisplayed()
        composeRule.onNodeWithText("1. To vomit.").assertIsDisplayed()
    }

    @Test
    fun example_is_shown_only_when_present() {
        val data = sampleWord()

        composeRule.setContent {
            WordInfoItem(wordInfo = data)
        }

        composeRule.onNodeWithText("Example: The cat slept all day.").assertIsDisplayed()

        // The second noun definition has no example, so count of "Example:" must be 1
        composeRule.onAllNodesWithText("Example:", substring = true)
            .assertCountEquals(1)
    }
}