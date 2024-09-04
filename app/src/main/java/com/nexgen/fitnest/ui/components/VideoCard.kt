package com.nexgen.fitnest.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
            .fillMaxWidth()
            .height(380.dp)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Transparent background
    ) {
        // Video Player
        VideoPlayer(
            videoResId = videoItem.videoResId,
            modifier = Modifier
                .fillMaxSize() // Fills the entire card
        )
    }
}
