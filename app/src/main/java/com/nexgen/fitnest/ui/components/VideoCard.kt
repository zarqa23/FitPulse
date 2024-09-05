package com.nexgen.fitnest.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nexgen.fitnest.data.models.VideoItem

@Composable
fun VideoCard(videoItem: VideoItem) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(16f / 9f)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Transparent background
    ) {
        // Video Player
        VideoPlayer(
            videoUrl = videoItem.videoUrl,
            modifier = Modifier
                .fillMaxSize() // Fills the entire card
        )
    }
}
