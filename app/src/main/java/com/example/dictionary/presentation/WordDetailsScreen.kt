package com.example.dictionary.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionary.presentation.viewmodel.WordDetailsViewModel

@Composable
fun WordDetailsScreen(
    word:String,
    viewModel: WordDetailsViewModel = hiltViewModel()
) {
    val data by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(word) {
        viewModel.localCache(word)
    }

    LazyColumn {
        items(data.wordInfoItems.size) { index ->
            WordInfoItem(data.wordInfoItems[index], modifier = Modifier.padding(16.dp))
        }
    }
}