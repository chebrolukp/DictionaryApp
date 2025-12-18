package com.example.dictionary.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionary.presentation.preview.MultiPreview
import com.example.dictionary.presentation.viewmodel.WordInfoViewModel
import timber.log.Timber

@Composable
fun HistoryScreen(
    viewModel: WordInfoViewModel,
    onItemClick: () -> Unit
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    Timber.d("history: $history")
    history.data?.let {
        HistoryList(it, onItemClick, onSearchClick = viewModel::searchFromHistory)
    }

}

@Composable
fun HistoryList(
    history : List<String>,
    onItemClick: () -> Unit,
    onSearchClick: (String) -> Unit
) {
    LazyColumn {
        items(history.size) { index ->
            val currentItem = history[index]
            currentItem.let {
                ListItem(
                    headlineContent = { Text(it) },
                    modifier = Modifier.clickable {
                        onSearchClick(it)
                        onItemClick()
                    }
                )
            }
        }
    }
}



@MultiPreview
@Composable
fun HistoryScreenPreview() {
    HistoryList(
        history = listOf("first", "second", "third"),
        onSearchClick = {},
        onItemClick = {}
    )
}