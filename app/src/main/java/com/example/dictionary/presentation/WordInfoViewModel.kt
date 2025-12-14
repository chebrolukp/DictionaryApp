package com.example.dictionary.presentation

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionary.R
import com.example.dictionary.core.util.DictionaryException
import com.example.dictionary.core.util.Resource
import com.example.dictionary.domain.usecase.GetSearchHistoryUseCase
import com.example.dictionary.domain.usecase.GetWordInfoUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordInfoViewModel @Inject constructor(
    private val getWordInfoUsecase: GetWordInfoUsecase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val application: Application
) : ViewModel() {
    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _state = MutableStateFlow(WordInfoState())
    val state = _state.asStateFlow()

    val history = getSearchHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Resource.Loading()
        )

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var searchJob: Job? = null

    fun onSearch(query: String){
        _searchQuery.value = query
    }

    fun onSearchClick(query: String = _searchQuery.value) {
        performSearch(query)
    }

    fun searchFromHistory(word: String) {
        _searchQuery.value = word
        performSearch(word, debounce = false)
    }


    private fun performSearch(query: String, debounce: Boolean = true) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            val minLoadingJob = launch {
                delay(MIN_LOADING_TIME)
            }
            if(debounce) delay(DEBOUNCE_DELAY)
            getWordInfoUsecase(query).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            wordInfoItems = result.data ?: emptyList(),
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            wordInfoItems = result.data ?: emptyList(),
                        )
                        val errorMessage = when (result.exception) {
                            is DictionaryException.NoInternetException ->
                                application.getString(R.string.error_no_internet)

                            is DictionaryException.WordNotFoundException ->
                                application.getString(R.string.error_word_not_found)

                            is DictionaryException.ServerException ->
                                application.getString(R.string.error_server)

                            else ->
                                application.getString(R.string.error_unknown)
                        }
                        _eventFlow.emit(
                            UIEvent.ShowSnackbar(errorMessage)
                        )
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            wordInfoItems = result.data ?: emptyList(),
                        )
                    }
                }

            }
            minLoadingJob.join()
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String) : UIEvent()
    }

    companion object {
        private const val MIN_LOADING_TIME = 1000L
        private const val DEBOUNCE_DELAY = 500L
    }
}