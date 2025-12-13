package com.example.dictionary.presentation

import android.app.Application
import app.cash.turbine.test
import com.example.dictionary.core.util.DictionaryException
import com.example.dictionary.core.util.Resource
import com.example.dictionary.domain.model.WordInfo
import com.example.dictionary.domain.usecase.GetSearchHistoryUseCase
import com.example.dictionary.domain.usecase.GetWordInfoUsecase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class WordInfoViewModelTest {

    private lateinit var getWordInfoUsecase: GetWordInfoUsecase
    private lateinit var getSearchHistoryUseCase: GetSearchHistoryUseCase
    private lateinit var app: Application
    private lateinit var viewModel: WordInfoViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getWordInfoUsecase = mock()
        getSearchHistoryUseCase = mock()
        app = mock()
        whenever(app.getString(any())).thenReturn("error")
        viewModel = WordInfoViewModel(getWordInfoUsecase, getSearchHistoryUseCase, app)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `typing triggers debounce before calling usecase`() = runTest {
        val query = "hello"

        // Mock usecase flow
        whenever(getWordInfoUsecase(query)).thenReturn(
            flowOf(Resource.Success(emptyList()))
        )

        viewModel.onSearch(query)

        // Advance less than debounce
        advanceTimeBy(300)
        verify(getWordInfoUsecase, never()).invoke(any())

        // Advance past debounce
        advanceTimeBy(300)
        verify(getWordInfoUsecase).invoke(query)
    }

    @Test
    fun `success updates state with results`() = runTest {
        val query = "cat"
        val resultItems = listOf(mock<WordInfo>())

        whenever(getWordInfoUsecase(query)).thenReturn(
            flowOf(Resource.Success(resultItems))
        )

        viewModel.onSearch(query)

        // Run debounce + completion
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(resultItems, state.wordInfoItems)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `error emits snackbar and keeps list`() = runTest {
        val query = "dog"

        whenever(getWordInfoUsecase(query)).thenReturn(
            flowOf(
                Resource.Error(
                    exception = DictionaryException.WordNotFoundException
                )
            )
        )

        viewModel.eventFlow.test {
            viewModel.onSearch(query)
            advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is WordInfoViewModel.UIEvent.ShowSnackbar)
            assertEquals("error", (event as WordInfoViewModel.UIEvent.ShowSnackbar).message)
        }
    }

    @Test
    fun `minimum loading time respected`() = runTest {
        val query = "fast"

        whenever(getWordInfoUsecase(query)).thenReturn(
            flowOf(Resource.Success(emptyList()))
        )

        viewModel.onSearch(query)

        // Debounce + success happens fast
        advanceTimeBy(600)

        // Loading should still be true because MIN_LOADING_TIME = 1000
        assertEquals(true, viewModel.state.value.isLoading)

        advanceTimeBy(500)

        // Now loading should end
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `new search cancels previous job`() = runTest {
        val query1 = "a"
        val query2 = "b"

        whenever(getWordInfoUsecase(any())).thenReturn(
            flow {
                delay(5000)  // Simulate long-running
                emit(Resource.Success(emptyList()))
            }
        )

        viewModel.onSearch(query1)
        advanceTimeBy(100)

        viewModel.onSearch(query2)
        advanceUntilIdle()

        // First should be cancelled, second should run
        verify(getWordInfoUsecase).invoke(query2)
        verify(getWordInfoUsecase, never()).invoke(query1)
    }

    @Test
    fun `history emits success with empty list`() = runTest {
        whenever(getSearchHistoryUseCase()).thenReturn(
            flowOf(Resource.Success(emptyList()))
        )

        viewModel = WordInfoViewModel(
            getWordInfoUsecase,
            getSearchHistoryUseCase,
            app
        )

        viewModel.history.test {
            // stateIn initial value
            assert(awaitItem() is Resource.Loading)

            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertTrue(success.data!!.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
