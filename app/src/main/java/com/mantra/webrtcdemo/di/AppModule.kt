package com.mantra.webrtcdemo.di

import android.app.Application
import android.content.Context
import com.mantra.webrtcdemo.data.remote.FirebaseSignaling
import com.mantra.webrtcdemo.presentation.webrtc.WebRtcManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// AppModule.kt
// Purpose: This is the main Hilt module where we provide all dependencies
//          that will be injected across the app (WebRtcManager, FirebaseSignaling, etc.)
// Why we need it: Hilt needs to know how to create objects like WebRtcManager
//                 so we don't manually create them in ViewModel.
@Module
@InstallIn(SingletonComponent::class)   // This module lives as long as the app is running
object AppModule {

    // Provide Application Context
    // Why? Many classes like WebRtcManager need Context to work with camera, audio, etc.
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    // Provide WebRtcManager
    // Explanation: WebRtcManager is the core class that creates PeerConnectionFactory,
    //              local video/audio tracks, and manages multiple PeerConnections.
    //              We make it Singleton because we want only one instance throughout the app.
    @Provides
    @Singleton
    fun provideWebRtcManager(context: Context): WebRtcManager {
        return WebRtcManager(context)
    }

    // Provide FirebaseSignaling
    // Explanation: This class handles all communication with Firebase Realtime Database
    //              (sending/receiving Offer, Answer, ICE candidates).
    //              Also made Singleton because one signaling handler is enough.
    @Provides
    @Singleton
    fun provideFirebaseSignaling(): FirebaseSignaling {
        return FirebaseSignaling()
    }

    // You can add more providers here later (e.g., for Matter SDK when we reach that part)
}