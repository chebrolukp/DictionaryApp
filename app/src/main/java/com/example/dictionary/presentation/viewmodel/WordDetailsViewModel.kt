package com.example.dictionary.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionary.domain.usecase.GetLocalWordInfoUsecase
import com.example.dictionary.presentation.WordInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordDetailsViewModel @Inject constructor(
    private val usecase: GetLocalWordInfoUsecase
):ViewModel() {

    private val _state = MutableStateFlow(WordInfoState())
    val state = _state.asStateFlow()
    private var job: Job? = null

    fun localCache(word: String){
        job?.cancel()
        job = viewModelScope.launch {
            val result = usecase(word)
            _state.value = if(result.isEmpty()){
                WordInfoState()
            } else {
                WordInfoState(wordInfoItems = result)
            }
        }
    }

}