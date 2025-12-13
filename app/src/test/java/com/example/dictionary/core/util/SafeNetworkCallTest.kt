package com.example.dictionary.core.util

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SafeNetworkCallTest {

    private val fallbackList = emptyList<String>()

    private fun flowUnderTest(
        cacheLoader: suspend () -> List<String>,
        networkCall: suspend () -> List<String>,
        cacheUpdater: suspend (List<String>) -> Unit = {},
        emptyCheck: (List<String>) -> Boolean = { false }
    ) = safeNetworkCall(
        cacheLoader = cacheLoader,
        networkCall = networkCall,
        cacheUpdater = cacheUpdater,
        emptyNetworkCheck = emptyCheck,
        fallbackValue = fallbackList
    )

    // ----------------------------------------------------------
    // SUCCESS
    // ----------------------------------------------------------

    @Test
    fun `emits loading, cached, success when network succeeds`() = runTest {
        val cache = listOf("cached")
        val remote = listOf("remote")

        val cacheLoader = mockk<suspend () -> List<String>>()
        val networkCall = mockk<suspend () -> List<String>>()
        val cacheUpdater = mockk<suspend (List<String>) -> Unit>()

        coEvery { cacheLoader() } returns cache andThen remote
        coEvery { networkCall() } returns remote
        coEvery { cacheUpdater(remote) } returns Unit

        flowUnderTest(cacheLoader, networkCall, cacheUpdater).test {
            assert(awaitItem() is Resource.Loading)
            assert(awaitItem().data == cache)

            val success = awaitItem()
            assert(success is Resource.Success)
            assert(success.data == remote)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------------------------------------------------
    // EMPTY REMOTE RESPONSE
    // ----------------------------------------------------------

    @Test
    fun `emits WordNotFoundException when emptyNetworkCheck returns true`() = runTest {
        val cache = listOf("cached")

        val cacheLoader = mockk<suspend () -> List<String>>()
        val networkCall = mockk<suspend () -> List<String>>()

        coEvery { cacheLoader() } returns cache
        coEvery { networkCall() } returns emptyList()

        flowUnderTest(
            cacheLoader,
            networkCall,
            emptyCheck = { it.isEmpty() }
        ).test {
            awaitItem()  // Loading
            awaitItem()  // Loading(cached)

            val error = awaitItem()
            assert(error is Resource.Error)
            assert(error.exception === DictionaryException.WordNotFoundException)
            assert(error.data == cache)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------------------------------------------------
    // ERROR CASES
    // ----------------------------------------------------------

    @Test
    fun `NoInternetException when UnknownHostException is thrown`() = runTest {
        val cache = listOf("cached")

        val cacheLoader = mockk<suspend () -> List<String>>()
        val networkCall = mockk<suspend () -> List<String>>()

        coEvery { cacheLoader() } returns cache
        coEvery { networkCall() } throws UnknownHostException()

        flowUnderTest(cacheLoader, networkCall).test {
            awaitItem()
            awaitItem()

            val error = awaitItem()
            assert(error is Resource.Error)
            assert(error.exception === DictionaryException.NoInternetException)
            assert(error.data == cache)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NetworkException when SocketTimeoutException is thrown`() = runTest {
        val cache = listOf("cached")

        val cacheLoader = mockk<suspend () -> List<String>>()
        val networkCall = mockk<suspend () -> List<String>>()

        coEvery { cacheLoader() } returns cache
        coEvery { networkCall() } throws SocketTimeoutException()

        flowUnderTest(cacheLoader, networkCall).test {
            awaitItem()
            awaitItem()

            val error = awaitItem()
            assert(error is Resource.Error)
            assert(error.exception === DictionaryException.NetworkException)
            assert(error.data == cache)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ServerException when HttpException is thrown`() = runTest {
        val cache = listOf("cached")

        val cacheLoader = mockk<suspend () -> List<String>>()
        val networkCall = mockk<suspend () -> List<String>>()
        val httpException = mockk<HttpException>()

        coEvery { cacheLoader() } returns cache
        coEvery { networkCall() } throws httpException

        flowUnderTest(cacheLoader, networkCall).test {
            awaitItem()
            awaitItem()

            val error = awaitItem()
            assert(error is Resource.Error)
            assert(error.exception === DictionaryException.ServerException)
            assert(error.data == cache)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NetworkException when IOException is thrown`() = runTest {
        val cache = listOf("cached")

        val cacheLoader = mockk<suspend () -> List<String>>()
        val networkCall = mockk<suspend () -> List<String>>()

        coEvery { cacheLoader() } returns cache
        coEvery { networkCall() } throws IOException()

        flowUnderTest(cacheLoader, networkCall).test {
            awaitItem()
            awaitItem()

            val error = awaitItem()
            assert(error is Resource.Error)
            assert(error.exception === DictionaryException.NetworkException)
            assert(error.data == cache)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UnknownException for all other exceptions`() = runTest {
        val cache = listOf("cached")

        val cacheLoader = mockk<suspend () -> List<String>>()
        val networkCall = mockk<suspend () -> List<String>>()

        coEvery { cacheLoader() } returns cache
        coEvery { networkCall() } throws RuntimeException("weird")

        flowUnderTest(cacheLoader, networkCall).test {
            awaitItem()
            awaitItem()

            val error = awaitItem()
            assert(error is Resource.Error)
            assert(error.exception === DictionaryException.UnknownException)
            assert(error.data == cache)

            cancelAndIgnoreRemainingEvents()
        }
    }
}