package com.mantra.webrtcdemo.presentation.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.mantra.webrtcdemo.data.remote.FirebaseSignaling
import com.mantra.webrtcdemo.presentation.webrtc.WebRtcManager
import dagger.hilt.android.lifecycle.HiltViewModel
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import org.webrtc.RtpTransceiver
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack
import javax.inject.Inject

// WebRtcViewModel.kt
// Purpose: This is the brain of the group call screen.
//          It connects WebRtcManager and FirebaseSignaling together.
//          It decides when to create PeerConnection, when to send offer, etc.
// Why MVVM: Keeps UI code clean and logic reusable.
@HiltViewModel
class WebRtcViewModel @Inject constructor(
    private val webRtcManager: WebRtcManager,
    private val firebaseSignaling: FirebaseSignaling
) : ViewModel() {

    private var roomId = ""
    private var myUserId = ""        // Example: "Ravi" or random string

    // List of all remote video tracks (for UI grid)
    val remoteVideoTracks = mutableStateMapOf<String, VideoTrack>()

    // First, define the PeerConnection.Observer
    // Why? Because signalingListener needs to use it when creating new PeerConnections.
    private val peerObserver = object : PeerConnection.Observer {
        // Deep comment: This observer tells us when remote video/audio track arrives from other users
        override fun onAddTrack(receiver: RtpReceiver?, mediaStreams: Array<out MediaStream>?) {
            mediaStreams?.forEach { stream ->
                stream.videoTracks.firstOrNull()?.let { videoTrack ->
                    //remoteVideoTracks[/* we will use receiver id later */] = videoTrack
                }
            }
        }
        // Other methods can be empty for basic demo (we will add logs later)
        override fun onIceCandidate(candidate: IceCandidate?) { /* handled by signaling */ }
        override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate?>?) {}
        override fun onSignalingChange(newState: PeerConnection.SignalingState?) {}
        override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {}
        override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {}
        override fun onDataChannel(dataChannel: DataChannel?) {}
        override fun onRenegotiationNeeded() {}
        override fun onAddStream(stream: MediaStream?) {}
        override fun onRemoveStream(stream: MediaStream?) {}
        override fun onIceConnectionReceivingChange(receiving: Boolean) {}
        override fun onTrack(transceiver: RtpTransceiver?) {}
    }

    // We will use this listener to react to signaling events
    private val signalingListener = object : FirebaseSignaling.SignalingListener {
        override fun onOfferReceived(
            fromUserId: String,
            offer: SessionDescription
        ) {
            // Explanation: Someone sent us an offer. We create PeerConnection and send answer back.
            //val peerConnection = webRtcManager.createPeerConnection(fromUserId, peerObserver))

        }

        override fun onAnswerReceived(fromUserId: String, answer: SessionDescription) {}
        override fun onIceCandidateReceived(fromUserId: String, candidate: IceCandidate) {}
        override fun onUserJoined(userId: String) {}
        override fun onUserLeft(userId: String) {}
    }

    // Called from UI when user clicks "Join Room"
    fun joinRoom(roomId: String, userId: String) {
        this.roomId = roomId
        this.myUserId = userId

        // Create local tracks once
        webRtcManager.createLocalVideoTrack()
        webRtcManager.createLocalAudioTrack()

        // Start listening to Firebase
        firebaseSignaling.joinRoom(roomId, userId, signalingListener)
    }

    fun leaveRoom() {
        firebaseSignaling.leaveRoom(roomId, myUserId)
        webRtcManager.dispose()
        remoteVideoTracks.clear()
    }

    override fun onCleared() {
        super.onCleared()
        leaveRoom()
    }
}