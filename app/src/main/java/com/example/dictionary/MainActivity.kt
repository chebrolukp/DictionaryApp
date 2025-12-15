package com.example.dictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dictionary.presentation.viewmodel.AudioPlayerViewModel
import com.example.dictionary.presentation.HistoryScreen
import com.example.dictionary.presentation.WordDetailsScreen
import com.example.dictionary.presentation.WordInfoItem
import com.example.dictionary.presentation.viewmodel.WordInfoViewModel
import com.example.dictionary.presentation.navigation.Screen
import com.example.dictionary.presentation.preview.MultiPreview
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DictionaryUI() {
        val viewModel: WordInfoViewModel = hiltViewModel()
        val snackBarHostState = remember { SnackbarHostState() }
        val searchQuery by viewModel.searchQuery
        val navController = rememberNavController()
        val currentRoute = currentRoute(navController)
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
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        DictionaryTitle()
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(snackBarHostState)
            },
            bottomBar = {
                if (currentRoute == Screen.Home.route ||
                    currentRoute == Screen.History.route
                ) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = currentRoute == Screen.Home.route,
                            onClick = {
                                navController.navigate(Screen.Home.route) {
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(Icons.Default.Home, null) },
                            label = { Text(stringResource(R.string.home)) }
                        )

                        NavigationBarItem(
                            selected = currentRoute == Screen.History.route,
                            onClick = {
                                navController.navigate(Screen.History.route)
                            },
                            icon = { Icon(Icons.Default.History, null) },
                            label = { Text(stringResource(R.string.history)) }
                        )
                    }
                }
            }) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    WordInfoScreen(
                        viewModel = viewModel,
                        searchQuery = searchQuery,
                        onSearch = viewModel::onSearch
                    )
                }
                composable(Screen.History.route) {
                    HistoryScreen(
                        viewModel = viewModel,
                        onItemClick = {
                            navController.navigate(Screen.Home.route) {
                                launchSingleTop = true
                            }
                        },
                        onLocalClick = { word ->
                            navController.navigate(Screen.WordDetails.create(word)){
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(
                    route = Screen.WordDetails.route,
                    arguments = listOf(navArgument("word") { type = NavType.StringType })
                ) { backStackEntry ->
                    val word = backStackEntry.arguments?.getString("word") ?: return@composable
                    WordDetailsScreen(word)
                }
            }
        }
    }
}

@Composable
private fun currentRoute(navController: androidx.navigation.NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
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

    Box(modifier = modifier.fillMaxSize()) {
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
                        WordInfoItem(state.wordInfoItems[index], modifier = Modifier.padding(16.dp),
                            onPlayAudio = { audioUrl ->
                                audioViewModel.play(audioUrl)
                            })
                    }
                }
            }
        }
    }
}

@MultiPreview
@Composable
fun DictionaryTitlePreview() {
    DictionaryTitle()
}