package com.mantra.webrtcdemo.presentation.webrtc

import android.content.Context
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import javax.inject.Inject
import javax.inject.Singleton

// WebRtcManager.kt - Supports multiple peers (Mesh Group Call)
@Singleton
class WebRtcManager @Inject constructor(private val context: Context) {

    private var peerConnectionFactory: PeerConnectionFactory? = null
    private val eglBase: EglBase = EglBase.create()

    // Local tracks (shared across all connections)
    var localVideoTrack: VideoTrack? = null
    var localAudioTrack: AudioTrack? = null
    private var videoCapturer: VideoCapturer? = null
    private var localVideoSource: VideoSource? = null

    // Map of remote PeerConnections: userId -> PeerConnection
    private val peerConnections = mutableMapOf<String, PeerConnection>()

    init {
        initializePeerConnectionFactory()
    }

    private fun initializePeerConnectionFactory() {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(PeerConnectionFactory.Options())
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .createPeerConnectionFactory()
    }

    /** Create Local Video Track (Camera) - called once */
    fun createLocalVideoTrack(): VideoTrack? {
        if (localVideoTrack != null) return localVideoTrack

        videoCapturer = createCameraCapturer(Camera2Enumerator(context))

        localVideoSource = peerConnectionFactory?.createVideoSource(videoCapturer!!.isScreencast)
        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)

        videoCapturer?.initialize(surfaceTextureHelper, context, localVideoSource!!.capturerObserver)
        videoCapturer?.startCapture(1280, 720, 30)  // 720p @ 30fps

        localVideoTrack = peerConnectionFactory?.createVideoTrack("local_video", localVideoSource)
        localVideoTrack?.setEnabled(true)
        return localVideoTrack
    }

    /** Create Local Audio Track - called once */
    fun createLocalAudioTrack(): AudioTrack? {
        if (localAudioTrack != null) return localAudioTrack

        val audioSource = peerConnectionFactory?.createAudioSource(MediaConstraints())
        localAudioTrack = peerConnectionFactory?.createAudioTrack("local_audio", audioSource)
        localAudioTrack?.setEnabled(true)
        return localAudioTrack
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        // Prefer front camera
        for (name in deviceNames) {
            if (enumerator.isFrontFacing(name)) {
                enumerator.createCapturer(name, null)?.let { return it }
            }
        }
        // Then back camera
        for (name in deviceNames) {
            if (!enumerator.isFrontFacing(name)) {
                enumerator.createCapturer(name, null)?.let { return it }
            }
        }
        return null
    }

    /** Create a new PeerConnection for a remote user */
    fun createPeerConnection(userId: String, observer: PeerConnection.Observer): PeerConnection? {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
            // Add your own TURN servers in production
        )

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }

        val peerConnection = peerConnectionFactory?.createPeerConnection(rtcConfig, observer)
        if (peerConnection != null) {
            peerConnections[userId] = peerConnection
        }
        return peerConnection
    }

    fun getPeerConnection(userId: String): PeerConnection? = peerConnections[userId]

    fun removePeerConnection(userId: String) {
        peerConnections[userId]?.close()
        peerConnections.remove(userId)
    }

    fun dispose() {
        videoCapturer?.stopCapture()
        videoCapturer?.dispose()
        localVideoSource?.dispose()
        localVideoTrack?.dispose()
        localAudioTrack?.dispose()
        peerConnections.values.forEach { it.close() }
        peerConnections.clear()
        eglBase.release()
    }
}