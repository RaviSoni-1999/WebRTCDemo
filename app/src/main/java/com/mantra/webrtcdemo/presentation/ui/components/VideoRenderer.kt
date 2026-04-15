package com.mantra.webrtcdemo.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

@Composable
fun VideoRenderer(
    videoTrack: VideoTrack?,
    modifier: Modifier = Modifier,
    isMirrored: Boolean = false,
    onRendererReady: ((SurfaceViewRenderer) -> Unit)? = null
) {
    var renderer by remember { mutableStateOf<SurfaceViewRenderer?>(null) }

    AndroidView(
        factory = { ctx ->
            SurfaceViewRenderer(ctx).apply {
                setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                setMirror(isMirrored)
                // Note: In a real app, you'd likely want to pass the EglBase.Context 
                // from your WebRtcManager to ensure everything shares the same context.
                init(EglBase.create().eglBaseContext, null)
                renderer = this
                onRendererReady?.invoke(this)
            }
        },
        modifier = modifier.background(Color.Black),
        update = { view ->
            videoTrack?.addSink(view)
        }
    )

    DisposableEffect(videoTrack) {
        onDispose {
            videoTrack?.removeSink(renderer)
            renderer?.release()
        }
    }
}