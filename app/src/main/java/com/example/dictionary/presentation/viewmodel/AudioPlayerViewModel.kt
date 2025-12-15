package com.example.dictionary.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val player = ExoPlayer.Builder(context).build()

    fun play(url: String) {
        stop()
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
    }

    fun stop() {
        if (player.isPlaying) {
            player.stop()
        }
        player.clearMediaItems()
    }


    override fun onCleared() {
        player.release()
    }
}
