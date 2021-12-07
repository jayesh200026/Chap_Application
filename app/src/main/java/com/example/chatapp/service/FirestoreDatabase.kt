package com.example.chatapp.service

import android.util.Log
import androidx.core.net.toUri
import com.example.chatapp.service.model.*
import com.example.chatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine

object FirestoreDatabase {
    suspend fun addUserDetails(user: User): Boolean {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                Log.d("uri", "" + user.uri)
                FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .set(user)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            cont.resumeWith(Result.success(true))
                        } else {
                            cont.resumeWith(Result.failure(it.exception as Exception))
                        }
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }

    suspend fun getUserdetails(): User {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener {
                        val name = it.get(Constants.COLUMN_NAME).toString()
                        val status = it.get(Constants.COLUMN_STATUS).toString()
                        val uri = it.get(Constants.COLUMN_URI).toString()
                        val user = User(userName = name, status = status, uri = uri)
                        cont.resumeWith(Result.success(user))

                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }

    suspend fun readChats(): MutableList<String> {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val list = mutableListOf<Chat>()
            val participantList = mutableListOf<String>()
            if (uid != null) {
                FirebaseFirestore.getInstance().collection("chats")
                    .whereArrayContains(Constants.COLUMN_PARTICIPANTS, uid)
                    .get()
                    .addOnSuccessListener {
                        for (documents in it) {
                            val list1 =
                                documents.get(Constants.COLUMN_PARTICIPANTS) as MutableList<String>
                            Log.d("participants", list1.toString())
                            list1.remove(uid)
                            participantList.add(list1[0])
//                            val receiver = documents.get(Constants.COLUMN_RECEIVER_ID).toString()
//                            val sender = documents.get(Constants.COLUMN_RECEIVER_ID).toString()
//                            val message = documents.get(Constants.COLUMN_MESSAGE).toString()
//                            val chat =
//                                Chat(senderId = sender, receiverId = receiver, message = message)
//                            list.add(chat)
                        }
                        cont.resumeWith(Result.success(participantList))
                    }
                    .addOnFailureListener {
                        Log.d("chat", "failed")
                    }
            }
        }

    }

    suspend fun readAllUsers(): MutableList<UserWithID> {
        return suspendCoroutine { cont ->
            val userList = mutableListOf<UserWithID>()
            FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnSuccessListener {
                    for (i in it.documents) {
                        val id = i.id
                        val name = i.get(Constants.COLUMN_NAME).toString()
                        val status = i.get(Constants.COLUMN_STATUS).toString()
                        val uri = i.get(Constants.COLUMN_URI).toString()
                        val user =
                            UserWithID(userId = id, userName = name, status = status, uri = uri)
                        userList.add(user)
                    }
                    cont.resumeWith(Result.success(userList))
                }
                .addOnFailureListener {
                    cont.resumeWith(Result.failure(it))
                }
        }
    }

    suspend fun getAllChats(participant: String?): MutableList<AllMessages> {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null && participant != null) {
                FirebaseFirestore.getInstance().collection("chats")
                    .whereArrayContains(Constants.COLUMN_PARTICIPANTS, uid)
                    .get()
                    .addOnSuccessListener {
                        Log.d("chatsfromdb", "inside chat")
                        CoroutineScope(Dispatchers.IO).launch {
                            val requests = ArrayList<Deferred<AllMessages>>()
                            for (doc in it) {
                                requests.add(async { getMessages(doc) })
                            }
                            val chats = requests.awaitAll()
                            cont.resumeWith(Result.success(chats.toMutableList()))
                            Log.d("chatsfromdb", chats.size.toString())
                        }
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                        Log.d("chatsfromdb", it.toString())
                    }
            }
        }
    }

    suspend fun getMessages(doc: QueryDocumentSnapshot?) =
        suspendCoroutine<AllMessages> { cont ->
            if (doc != null) {
                doc.reference.collection(Constants.MESSAGES)
                    .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {
                        val msgList = arrayListOf<Chats>()
                        for (msg in it.documents) {
                            Log.d("chatsfromdb", msg.getString(Constants.MESSAGEID)!!)
                            msgList.add(
                                Chats(
                                    messageId = msg.getString(Constants.MESSAGEID)!!,
                                    senderId = msg.getString(Constants.SENDERID)!!,
                                    receiverId = msg.getString(Constants.RECEIVERID)!!,
                                    sentTime = msg.getString(Constants.SENT_TIME)!!.toLong(),
                                    message = msg.getString(Constants.TEXT)!!,
                                    messageType = msg.getString(Constants.MESSAGE_TYPE)!!
                                )
                            )
                        }
                        val chat =
                            AllMessages(doc.get(Constants.COLUMN_PARTICIPANTS) as ArrayList<String>,
                            msgList)
                        Log.d("chatsfromdb", "hd")
                        cont.resumeWith(Result.success(chat))
                    }
            }
        }


}