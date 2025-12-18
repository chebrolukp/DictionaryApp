package com.example.dictionary.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dictionary.DictionaryTitle
import com.example.dictionary.R
import com.example.dictionary.presentation.viewmodel.AudioPlayerViewModel
import com.example.dictionary.presentation.viewmodel.WordInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordInfoScreen(
    modifier: Modifier = Modifier,
    viewModel: WordInfoViewModel,
    searchQuery: String = "",
    onSearch: (String) -> Unit = {}
) {
    val audioViewModel: AudioPlayerViewModel = hiltViewModel()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    // Stop audio when leaving the screen
    DisposableEffect(Unit) {
        onDispose { audioViewModel.stop() }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    DictionaryTitle()
                }
            )
        }) { innerPadding ->
        Box(modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            Column(modifier = Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { onSearch(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = {
                        Text(stringResource(R.string.search))
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onSearchClick() }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Blue,
                    )
                )
                if (!state.isLoading && state.wordInfoItems.isNotEmpty()) {
                    // Display word info
                    LazyColumn {
                        items(state.wordInfoItems.size) { index ->
                            WordInfoItem(state.wordInfoItems[index],
                                modifier = Modifier.padding(16.dp),
                                onPlayAudio = { audioUrl ->
                                    audioViewModel.play(audioUrl)
                                })
                        }
                    }
                }
            }
        }
    }
}