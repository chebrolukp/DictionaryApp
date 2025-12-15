package com.example.dictionary.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionary.presentation.viewmodel.WordInfoViewModel
import timber.log.Timber

@Composable
fun HistoryScreen(
    viewModel: WordInfoViewModel,
    onItemClick: () -> Unit,
    onLocalClick: (String) -> Unit
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    Timber.d("history: $history")

    LazyColumn {
        items(history.data?.size ?: 0) { index ->
            val currentItem = history.data?.get(index)
            currentItem?.let {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        it,
                        modifier = Modifier.clickable {
                            viewModel.searchFromHistory(it)
                            onItemClick()
                        }.weight(1f),
                        maxLines = 1
                    )
                    IconButton(
                        onClick = { onLocalClick(it) }
                    ) {
                        Icon(Icons.Default.AccessTimeFilled, "view cached result")
                    }
                }
            }
        }
    }
}