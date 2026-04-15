package com.mantra.webrtcdemo.data.remote

import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import javax.inject.Inject
import javax.inject.Singleton

// FirebaseSignaling.kt
// Purpose: This class handles all communication with Firebase Realtime Database.
//          It is responsible for sending and receiving Offer, Answer, and ICE candidates
//          so that multiple users can connect to each other in the same room.
// Why we need it: WebRTC cannot discover other users by itself. This is the "signaling" part.
@Singleton
class FirebaseSignaling @Inject constructor() {

    private val database = Firebase.database
    private val roomsRef = database.getReference("rooms")   // Main node in Firebase

    // Callback interface so ViewModel can react when something arrives from Firebase
    interface SignalingListener {
        fun onOfferReceived(fromUserId: String, offer: SessionDescription)
        fun onAnswerReceived(fromUserId: String, answer: SessionDescription)
        fun onIceCandidateReceived(fromUserId: String, candidate: IceCandidate)
        fun onUserJoined(userId: String)                     // New user joined the room
        fun onUserLeft(userId: String)                       // User left the room
    }

    // Join a room and start listening for other users
    // Explanation: When user joins a room, we create a node for him and listen to offers/answers from others.
    fun joinRoom(
        roomId: String,
        myUserId: String,                    // Example: "Ravi" or "User123"
        listener: SignalingListener
    ) {
        val roomRef = roomsRef.child(roomId)

        // Step A: Create my own node so others can send me offers
        roomRef.child(myUserId).setValue(mapOf("online" to true))

        // Step B: Listen for any new offers sent to me
        roomRef.child(myUserId).child("offer").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val offerMap = snapshot.getValue<Map<String, Any>>() ?: return
                val sdp = offerMap["sdp"] as? String ?: return
                val type = offerMap["type"] as? String ?: return
                val offer = SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(type),
                    sdp
                )
                listener.onOfferReceived(offerMap["from"] as? String ?: "", offer)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // Step C: Listen for answers sent to me
        roomRef.child(myUserId).child("answer").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val answerMap = snapshot.getValue<Map<String, Any>>() ?: return
                val sdp = answerMap["sdp"] as? String ?: return
                val type = answerMap["type"] as? String ?: return
                val answer = SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(type),
                    sdp
                )
                listener.onAnswerReceived(answerMap["from"] as? String ?: "", answer)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        // Step D: Listen for ICE candidates sent to me
        roomRef.child(myUserId).child("ice").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val candidateMap = snapshot.getValue<Map<String, Any>>() ?: return
                val sdpMid = candidateMap["sdpMid"] as? String
                val sdpMLineIndex = (candidateMap["sdpMLineIndex"] as? Long)?.toInt() ?: 0
                val candidate = candidateMap["candidate"] as? String ?: return

                val iceCandidate = IceCandidate(sdpMid, sdpMLineIndex, candidate)
                listener.onIceCandidateReceived(candidateMap["from"] as? String ?: "", iceCandidate)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        // Step E: Listen when new user joins the room (so we can create PeerConnection to him)
        roomRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                p1: String?
            ) {
                val userId = snapshot.key ?: return
                if (userId != myUserId) {
                    listener.onUserJoined(userId)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val userId = snapshot.key ?: return
                if (userId != myUserId) {
                    listener.onUserLeft(userId)
                }
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                p1: String?
            ) {}

            override fun onChildMoved(
                p0: DataSnapshot,
                p1: String?
            ) {}

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    // Send Offer to a specific user
    fun sendOffer(roomId: String, toUserId: String, fromUserId: String, offer: SessionDescription) {
        val map = mapOf(
            "sdp" to offer.description,
            "type" to offer.type.canonicalForm(),
            "from" to fromUserId
        )
        roomsRef.child(roomId).child(toUserId).child("offer").setValue(map)
    }

    // Send ICE candidate to a specific user
    fun sendIceCandidate(roomId: String, toUserId: String, fromUserId: String, candidate: IceCandidate) {
        val map = mapOf(
            "sdpMid" to candidate.sdpMid,
            "sdpMLineIndex" to candidate.sdpMLineIndex,
            "candidate" to candidate.sdp,
            "from" to fromUserId
        )
        roomsRef.child(roomId).child(toUserId).child("ice").push().setValue(map)
    }

    // Leave room (clean up)
    fun leaveRoom(roomId: String, myUserId: String) {
        roomsRef.child(roomId).child(myUserId).removeValue()
    }
}