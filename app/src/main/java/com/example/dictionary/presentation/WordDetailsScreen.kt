package com.example.dictionary.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionary.presentation.viewmodel.WordDetailsViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailsScreen(
    word:String,
    onBack: () -> Unit,
    viewModel: WordDetailsViewModel = hiltViewModel()
) {
    val data by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(word) {
        viewModel.localCache(word)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(word) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(data.wordInfoItems.size) { index ->
                WordInfoItem(data.wordInfoItems[index], modifier = Modifier.padding(16.dp))
            }
        }
    }
}