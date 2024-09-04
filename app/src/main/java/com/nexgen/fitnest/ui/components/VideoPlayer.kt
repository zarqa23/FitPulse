package com.nexgen.fitnest.ui.components

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoResId: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isPlaying = remember { mutableStateOf(false) }
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, Uri.parse("android.resource://${context.packageName}/$videoResId"))

    // Extract the thumbnail
    val bitmap = retriever.frameAtTime
    retriever.release()

    // State to manage whether to show thumbnail or PlayerView
    var showThumbnail by remember { mutableStateOf(true) }

    // Create and remember an ExoPlayer instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/$videoResId"))
            prepare()
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            playWhenReady = false // Do not play immediately
        }
    }

    // Dispose of the ExoPlayer when the composable is removed from the composition
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier) {
        if (showThumbnail) {
            // Show the thumbnail image initially
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            showThumbnail = false
                            isPlaying.value = true
                            exoPlayer.playWhenReady = true // Start video playback
                        },
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        } else {
            // Show the ExoPlayer and prepare it for playback
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = true // Show media controls
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT// Fit the video within the layout
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}