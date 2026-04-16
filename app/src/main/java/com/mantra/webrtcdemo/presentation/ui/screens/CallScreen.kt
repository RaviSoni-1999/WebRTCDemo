package com.mantra.webrtcdemo.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.mantra.webrtcdemo.R
import com.mantra.webrtcdemo.presentation.ui.components.CallControls
import com.mantra.webrtcdemo.presentation.ui.components.VideoBox
import com.mantra.webrtcdemo.presentation.viewmodel.WebRtcViewModel

// CallScreen.kt
// Purpose: This is the main screen shown when user joins a room.
//          It displays local video + all remote users' videos in a grid.
//          Also contains call controls (mute, camera, switch camera, chat, end call).

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallScreen(
    roomId: String,
    userName: String,                    // Your display name
    navController: NavController,
    viewModel: WebRtcViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Observe remote video tracks from ViewModel
    val remoteTracks by lazy { viewModel.remoteVideoTracks }
    val isMicrophoneEnabled by remember { mutableStateOf(true) }
    val isCameraEnabled by remember { mutableStateOf(true) }
    var showChat by remember { mutableStateOf(false) }

    // Join the room when screen is launched
    LaunchedEffect(roomId, userName) {
        viewModel.joinRoom(roomId = roomId, userId = userName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Room: $roomId   (${remoteTracks.size + 1} participants)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ====================== VIDEO GRID AREA ======================
            // This grid shows Local video + all Remote videos
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (remoteTracks.isEmpty()) {
                    // When only you are in the room
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.waiting_join),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    // Grid layout for multiple users (2 or more)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),           // 2 columns
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Show Local Video (Your own camera)
                        item {
                            VideoBox(
                                title = stringResource(R.string.user_you, userName),
                                videoTrack = viewModel.webRtcManager.localVideoTrack,
                                isMirrored = true,
                                isLocal = true
                            )
                        }
                    }
                }
            }
        }

        // ====================== BOTTOM CONTROLS ======================
        CallControls(
            isMicrophoneEnabled = isMicrophoneEnabled,
            isCameraEnabled = isCameraEnabled,
            showChat = showChat,
            onMicrophoneToggle = {  /*TODO: Implement mute logic */ },
            onCameraToggle = {  /*TODO: Implement camera on/off */ },
            onSwitchCamera = {  /*TODO: Switch front/back camera*/ },
            onChatToggle = { showChat = !showChat },
            onEndCall = {
                viewModel.leaveRoom()
                navController.popBackStack()
            }
        )

        // ====================== IN-CALL CHAT ======================
        if (showChat) {
           /* InCallChatPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )*/
        }
    }
}