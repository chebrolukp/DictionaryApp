package com.example.dictionary.data.repository

import app.cash.turbine.test
import com.example.dictionary.core.util.DictionaryException
import com.example.dictionary.core.util.NetworkHelper
import com.example.dictionary.core.util.Resource
import com.example.dictionary.data.local.WordInfoDao
import com.example.dictionary.data.local.entity.WordInfoEntity
import com.example.dictionary.data.remote.DictionaryApi
import com.example.dictionary.data.remote.dto.LicenseDto
import com.example.dictionary.data.remote.dto.WordInfoDto
import com.example.dictionary.domain.model.License
import com.example.dictionary.domain.model.WordInfo
import com.example.dictionary.domain.repository.WordInfoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WordInfoRepositoryImplTest {

    private val api: DictionaryApi = mockk()
    private val dao: WordInfoDao = mockk()
    private val networkHelper: NetworkHelper = mockk()

    private lateinit var repo: WordInfoRepository

    private val dispatcher = StandardTestDispatcher()

    private val sampleWord = "cat"

    private val cachedWordInfo = WordInfo(
        word = "cat",
        phonetic = "/kat/",
        meanings = emptyList(),
        license = License("name1","url1"),
        phonetics = emptyList(),
        sourceUrls = emptyList()
    )

    private val cachedEntity = mockk<WordInfoEntity>()

    private val remoteDto = WordInfoDto(
        word = "cat",
        phonetic = "/kat/",
        meanings = emptyList(),
        license = LicenseDto("name1","url1"),
        sourceUrls = emptyList(),
        phonetics = emptyList()
    )

    @Before
    fun setup() = runTest {
        every { cachedEntity.toWordInfo() } returns cachedWordInfo

        coEvery { dao.getWordInfo(sampleWord) } returns listOf(cachedEntity)

        repo = WordInfoRepositoryImpl(api, dao, networkHelper)
    }

    @Test
    fun `emits loading then cached data then success when network available`() = runTest(dispatcher) {
        coEvery { networkHelper.isNetworkAvailable() } returns true
        coEvery { api.getWordInfo(sampleWord) } returns listOf(remoteDto)

        val newEntity = mockk<WordInfoEntity>()
        every { newEntity.toWordInfo() } returns cachedWordInfo

        coEvery { dao.deleteWordInfos(any()) } returns Unit
        coEvery { dao.insertWordInfos(any()) } returns Unit
        coEvery { dao.getWordInfo(sampleWord) } returns listOf(newEntity)

        repo.getWordInfo(sampleWord).test {
            assert(awaitItem() is Resource.Loading)
            assert(awaitItem().data == listOf(cachedWordInfo))
            assert(awaitItem() is Resource.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `returns NoInternetException when network is unavailable`() = runTest(dispatcher) {
        coEvery { networkHelper.isNetworkAvailable() } returns false

        repo.getWordInfo(sampleWord).test {
            awaitItem() // Loading
            awaitItem() // Loading(data=cached)

            val error = awaitItem()
            assert(error is Resource.Error)
            assert(error.exception is DictionaryException.NoInternetException)

            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `returns WordNotFoundException when API returns empty list`() = runTest(dispatcher) {
        coEvery { networkHelper.isNetworkAvailable() } returns true
        coEvery { api.getWordInfo(sampleWord) } returns emptyList()

        repo.getWordInfo(sampleWord).test {
            awaitItem()
            awaitItem()

            val error = awaitItem()
            assert(error is Resource.Error)
            assert(error.exception is DictionaryException.WordNotFoundException)

            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `falls back to cached data when network throws error`() = runTest(dispatcher) {
        coEvery { networkHelper.isNetworkAvailable() } returns true
        coEvery { api.getWordInfo(sampleWord) } throws RuntimeException("API fail")

        repo.getWordInfo(sampleWord).test {
            awaitItem()
            awaitItem()
            val result = awaitItem()
            assert(result is Resource.Error)
            assert(result.data == listOf(cachedWordInfo))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cache update happens when network succeeds`() = runTest(dispatcher) {
        coEvery { networkHelper.isNetworkAvailable() } returns true
        coEvery { api.getWordInfo(sampleWord) } returns listOf(remoteDto)

        coEvery { dao.deleteWordInfos(any()) } returns Unit
        coEvery { dao.insertWordInfos(any()) } returns Unit

        repo.getWordInfo(sampleWord).test {
            awaitItem()
            awaitItem()
            awaitItem()

            coVerify { dao.deleteWordInfos(any()) }
            coVerify { dao.insertWordInfos(any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }
}