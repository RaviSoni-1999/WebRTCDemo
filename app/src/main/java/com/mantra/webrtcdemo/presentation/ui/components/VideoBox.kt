package com.mantra.webrtcdemo.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.webrtc.VideoTrack

// VideoBox.kt
// Purpose: A clean card-like container for each video (local or remote)
//          Shows video + user name at the bottom.

@Composable
fun VideoBox(
    title: String,
    videoTrack: VideoTrack?,
    isMirrored: Boolean,
    isLocal: Boolean
) {
    Card(
        modifier = Modifier
            .aspectRatio(16f / 9f)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Render the actual video
            VideoRenderer(
                videoTrack = videoTrack,
                isMirrored = isMirrored,
                modifier = Modifier.fillMaxSize()
            )

            // Overlay username at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Show "Local" label if it's your own video
            if (isLocal) {
                Text(
                    text = "LOCAL",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Green.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
        }
    }
}
