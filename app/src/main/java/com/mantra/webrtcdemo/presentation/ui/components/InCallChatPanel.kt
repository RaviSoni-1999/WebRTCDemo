package com.mantra.webrtcdemo.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mantra.webrtcdemo.presentation.viewmodel.WebRtcViewModel
import kotlin.collections.emptyList

// InCallChatPanel.kt
// Purpose: This is the in-call chat panel that slides up from the bottom.
//          It allows users to send and receive real-time text messages
//          using WebRTC DataChannel during the video/audio call.
//          This works for group chat (all users in the same room can see messages).

/*
@Composable
fun InCallChatPanel(
    modifier: Modifier = Modifier,
    viewModel: WebRtcViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState(initial = emptyList())
    val textState = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {

        // Header
        Text(
            text = "Group Chat",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Divider()

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            reverseLayout = true   // New messages appear at bottom
        ) {
            items(messages) { message ->
               // ChatMessageItem(message = message)
            }
        }

        // Message Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (textState.value.isNotBlank()) {
                            //viewModel.sendChatMessage(textState.value.trim())
                            textState.value = ""
                            focusManager.clearFocus()
                        }
                    }
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                if (textState.value.isNotBlank()) {
                    //viewModel.sendChatMessage(textState.value.trim())
                    textState.value = ""
                    focusManager.clearFocus()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send Message",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}*/
