package com.example.chatapp.service

import android.util.Log
import com.example.chatapp.service.model.*
import com.example.chatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
                            AllMessages(
                                doc.get(Constants.COLUMN_PARTICIPANTS) as ArrayList<String>,
                                msgList
                            )
                        Log.d("chatsfromdb", "hd")
                        cont.resumeWith(Result.success(chat))
                    }
            }
        }

    suspend fun addNewMessage(receiver: String?, message: String) {
        return suspendCoroutine {
            val sender = FirebaseAuth.getInstance().currentUser?.uid
            val time = System.currentTimeMillis()
            if (sender != null && receiver != null) {
                val key = getDocumentKey(receiver, sender)
                FirebaseFirestore.getInstance().collection("chats")
                    .document(key)
                    .get()
                    .addOnSuccessListener {
                        val reference = it.reference
                        val participantslist = arrayListOf(sender, receiver)
                        val participantsMap = hashMapOf("participants" to participantslist)
                        reference.set(participantsMap)
                        reference.collection(Constants.MESSAGES)
                            .document()
                            .get()
                            .addOnSuccessListener {
                                val messageId = it.id
                                val chat = Chats(
                                    messageId = messageId,
                                    receiverId = receiver,
                                    senderId = sender,
                                    message = message,
                                    messageType = "text",
                                    sentTime = time
                                )
                                it.reference.set(chat)
                                    .addOnSuccessListener {
                                        Log.d("add", "added new chat")
                                    }
                            }
                    }
            }
        }


    }

    private fun getDocumentKey(receiver: String, sender: String): String {
        if (sender > receiver) {
            return sender + "_" + receiver
        } else {
            return receiver + "_" + sender
        }
    }

    @ExperimentalCoroutinesApi
    fun subscribeToListener(receiver: String?): Flow<Chats?> {
        return callbackFlow<Chats?> {
            val sender = FirebaseAuth.getInstance().currentUser?.uid
            if (sender != null && receiver != null) {
                val getdocumentkey = getDocumentKey(receiver, sender)
                val ref = FirebaseFirestore.getInstance().collection("chats")
                    .document(getdocumentkey)
                    .collection(Constants.MESSAGES)
                    .orderBy(Constants.SENT_TIME, Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            cancel("error fetching collection data at path", error)
                        }
                        if (snapshot != null) {
                            for (document in snapshot.documentChanges) {
                                if (document.type == DocumentChange.Type.ADDED) {
                                    val messageid =
                                        document.document.get(Constants.COLUMN_MESSAGE_ID)
                                            .toString()
                                    val senderId =
                                        document.document.get(Constants.COLUMN_SENDER_ID).toString()
                                    val receiverId =
                                        document.document.get(Constants.COLUMN_RECEIVER_ID)
                                            .toString()
                                    val message =
                                        document.document.get(Constants.COLUMN_MESSAGE).toString()
                                    val messageType =
                                        document.document.get(Constants.COLUMN_MESSAGE_TYPE)
                                            .toString()
                                    val sentTime =
                                        document.document.get(Constants.SENT_TIME).toString()
                                            .toLong()
                                    val chat = Chats(
                                        messageid,
                                        senderId,
                                        receiverId,
                                        message,
                                        messageType,
                                        sentTime
                                    )
                                    Log.d("add", "fetching notes")
                                    offer(chat)
                                }
                            }
                        }
                    }
                awaitClose {
                    ref.remove()
                }

            }
        }


    }

    suspend fun getGroups(): MutableList<GroupDetails> {
        return suspendCoroutine { cont ->
            val grpList = mutableListOf<GroupDetails>()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                FirebaseFirestore.getInstance().collection(Constants.GROUP_CHAT)
                    .whereArrayContains(Constants.COLUMN_PARTICIPANTS, userId)
                    .get()
                    .addOnSuccessListener {
                        for (document in it) {
                            val id = document.id
                            val groupName = document.get(Constants.NAME).toString()
                            val grp = GroupDetails(id, groupName)
                            grpList.add(grp)
                        }
                        cont.resumeWith(Result.success(grpList))
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }

    fun subscribeToGroup(groupId: String?): Flow<GroupChat?> {
        return callbackFlow<GroupChat?> {
            if (groupId != null) {
                val ref = FirebaseFirestore.getInstance().collection(Constants.GROUP_CHAT)
                    .document(groupId)
                    .collection(Constants.MESSAGES)
                    .orderBy(Constants.SENT_TIME, Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            cancel("error fetching collection data at path", error)
                        }
                        if (snapshot != null) {
                            for (document in snapshot.documentChanges) {
                                if (document.type == DocumentChange.Type.ADDED) {
                                    val messageid =
                                        document.document.get(Constants.COLUMN_MESSAGE_ID)
                                            .toString()
                                    val senderId =
                                        document.document.get(Constants.COLUMN_SENDER_ID).toString()
                                    val message =
                                        document.document.get(Constants.COLUMN_MESSAGE).toString()
                                    val messageType =
                                        document.document.get(Constants.COLUMN_MESSAGE_TYPE)
                                            .toString()
                                    val sentTime =
                                        document.document.get(Constants.SENT_TIME).toString()
                                            .toLong()
                                    val chat = GroupChat(
                                        messageId = messageid,
                                        senderId = senderId,
                                        message = message,
                                        messageType = messageType,
                                        sentTime = sentTime
                                    )
                                    offer(chat)
                                }
                            }
                        }
                    }
                awaitClose {
                    ref.remove()
                }
            }
        }
    }

    suspend fun addnewgrpMessage(groupId: String?, message: String): Boolean {
        return suspendCoroutine { cont ->
            val time = System.currentTimeMillis()
            val senderId = FirebaseAuth.getInstance().currentUser?.uid
            if (groupId != null && senderId != null) {
                FirebaseFirestore.getInstance().collection(Constants.GROUP_CHAT)
                    .document(groupId)
                    .get()
                    .addOnSuccessListener {
                        val reference = it.reference
                        reference.collection(Constants.MESSAGES)
                            .document()
                            .get()
                            .addOnSuccessListener {
                                val messageId = it.id
                                val message = GroupChat(
                                    message, messageId,
                                    "text", senderId, time
                                )
                                it.reference.set(message)
                                    .addOnSuccessListener {
                                        cont.resumeWith(Result.success(true))
                                    }
                            }
                    }
            }
        }

    }

    fun getAllUsersFromDb(): Flow<ArrayList<UserWithID>?> {

        return callbackFlow {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val userList = ArrayList<UserWithID>()
            val ref = FirebaseFirestore.getInstance().collection("users")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        this.offer(null)
                        error.printStackTrace()
                    } else {
                        if (snapshot != null) {
                            for (doc in snapshot.documentChanges) {
                                if (doc.type == DocumentChange.Type.ADDED) {
                                    val item = doc.document
                                    if (item.id == uid) {
                                        continue
                                    } else {
                                        val id = item.id.toString()
                                        val name = item.get(Constants.COLUMN_NAME).toString()
                                        val status = item.get(Constants.COLUMN_STATUS).toString()
                                        val uri = item.get(Constants.COLUMN_URI).toString()
                                        val user = UserWithID(id, name, status, uri)
                                        userList.add(user)
                                    }
                                }
                            }
                            this.offer(userList)
                        }
                    }
                }
            awaitClose {
                ref.remove()
            }
        }

    }

    suspend fun createGrp(name: String,list: ArrayList<String>?): Boolean {
        return suspendCoroutine {cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if(uid != null && list != null){
                list.add(uid)
                val group = CreateGroup(name,list)
                FirebaseFirestore.getInstance().collection(Constants.GROUP_CHAT)
                    .document()
                    .set(group)
                    .addOnSuccessListener {
                        cont.resumeWith(Result.success(true))
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }

    }
}


