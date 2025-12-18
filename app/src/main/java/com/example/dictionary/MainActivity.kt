package com.example.dictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.dictionary.presentation.HistoryScreen
import com.example.dictionary.presentation.WordDetailsScreen
import com.example.dictionary.presentation.WordInfoScreen
import com.example.dictionary.presentation.navigation.Destination
import com.example.dictionary.presentation.preview.MultiPreview
import com.example.dictionary.presentation.viewmodel.WordInfoViewModel
import com.example.dictionary.ui.theme.DictionaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DictionaryTheme {
                DictionaryUI()
            }
        }
    }

    @Composable
    fun DictionaryUI() {
        val viewModel: WordInfoViewModel = hiltViewModel()
        val snackBarHostState = remember { SnackbarHostState() }
        val searchQuery by viewModel.searchQuery

        val backStack = remember {
            mutableStateListOf<Destination>(Destination.Home)
        }
        val currentDestination = backStack.last()

        LaunchedEffect(key1 = Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is WordInfoViewModel.UIEvent.ShowSnackbar -> {
                        snackBarHostState.showSnackbar(
                            message = event.message
                        )
                    }
                }
            }
        }
        Scaffold(modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(snackBarHostState)
            },
            bottomBar = {
                if (currentDestination is Destination.Home ||
                    currentDestination is Destination.History
                ) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentDestination is Destination.Home,
                            onClick = {
                                backStack.clear()
                                backStack.add(Destination.Home)
                            },
                            icon = { Icon(Icons.Default.Home, null) },
                            label = { Text(stringResource(R.string.home)) }
                        )

                        NavigationBarItem(
                            selected = currentDestination is Destination.History,
                            onClick = {
                                backStack.clear()
                                backStack.add(Destination.History)
                            },
                            icon = { Icon(Icons.Default.History, null) },
                            label = { Text(stringResource(R.string.history)) }
                        )
                    }
                }
            }) { innerPadding ->
            Box(Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                when (currentDestination) {

                    is Destination.Home -> {
                        WordInfoScreen(
                            viewModel = viewModel,
                            searchQuery = searchQuery,
                            onSearch = viewModel::onSearch
                        )
                    }

                    is Destination.History -> {
                        HistoryScreen(
                            viewModel = viewModel,
                            onItemClick = {
                                backStack.clear()
                                backStack.add(Destination.Home)
                            },
                            onLocalClick = { word ->
                                backStack.add(Destination.WordDetails(word))
                            }
                        )
                    }

                    is Destination.WordDetails -> {
                        WordDetailsScreen(
                            word = currentDestination.word,
                            onBack = {
                                backStack.remove(currentDestination)
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun DictionaryTitle() {
    val title = stringResource(id = R.string.app_name)
    val firstLetterSize = dimensionResource(id = R.dimen.title_first_letter_size).value.sp
    val restTextSize = dimensionResource(id = R.dimen.title_rest_text_size).value.sp

    androidx.compose.foundation.text.BasicText(
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = colorResource(R.color.purple_700),
                    fontSize = firstLetterSize,
                    fontWeight = FontWeight.Bold,
                    baselineShift = BaselineShift.None
                )
            ) {
                append(title.first()) // First character "D"
            }
            withStyle(
                style = SpanStyle(
                    fontSize = restTextSize,
                    baselineShift = BaselineShift(0.15f)
                )
            ) {
                append(title.substring(1)) // Rest of the text "ictionary"
            }
        }
    )
}

@MultiPreview
@Composable
fun DictionaryTitlePreview() {
    DictionaryTitle()
}