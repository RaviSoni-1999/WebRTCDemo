package com.mantra.webrtcdemo.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mantra.webrtcdemo.R

// CallControls.kt
// Simple reusable row of call control buttons

@Composable
fun CallControls(
    isMicrophoneEnabled: Boolean,
    isCameraEnabled: Boolean,
    showChat: Boolean,
    onMicrophoneToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onSwitchCamera: () -> Unit,
    onChatToggle: () -> Unit,
    onEndCall: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Microphone Button
        IconButton(onClick = onMicrophoneToggle) {
            Icon(
                imageVector = if (isMicrophoneEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = stringResource(R.string.mic),
                tint = if (isMicrophoneEnabled) Color.Green else Color.Red,
                modifier = Modifier.size(32.dp)
            )
        }

        // Camera Button
        IconButton(onClick = onCameraToggle) {
            Icon(
                imageVector = if (isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                contentDescription = stringResource(R.string.camera),
                tint = if (isCameraEnabled) Color.Green else Color.Red,
                modifier = Modifier.size(32.dp)
            )
        }

        // Switch Camera
        IconButton(onClick = onSwitchCamera) {
            Icon(Icons.Default.FlipCameraAndroid, "Switch Camera", modifier = Modifier.size(32.dp))
        }

        // End Call Button (Red)
        IconButton(onClick = onEndCall) {
            Icon(
                imageVector = Icons.Default.CallEnd,
                contentDescription = stringResource(R.string.end_call),
                tint = Color.Red,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}