package com.example.dictionary.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionary.presentation.viewmodel.WordInfoViewModel
import timber.log.Timber

@Composable
fun HistoryScreen(
    viewModel: WordInfoViewModel,
    onItemClick: () -> Unit
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    Timber.d("history: $history")

    LazyColumn {
        items(history.data?.size ?: 0) { index ->
            val currentItem = history.data?.get(index)
            currentItem?.let {
                ListItem(
                    headlineContent = { Text(it) },
                    modifier = Modifier.clickable {
                        viewModel.searchFromHistory(it)
                        onItemClick()
                    }
                )
            }
        }
    }
}