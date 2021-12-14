package com.example.chatapp.service

import android.util.Log
import com.example.chatapp.service.model.*
import com.example.chatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.suspendCoroutine

object FirestoreDatabase {
    suspend fun addUserDetails(user: User, token: String?): Boolean {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null && token != null) {
                Log.d("uri", "" + user.uri)
                val userWithToken = UserWithToken(user.userName, user.status, user.uri, token)
                FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .set(userWithToken)
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
                        val name = it.get(Constants.NAME).toString()
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

    suspend fun getParticipantDetails(participantId: String?): User {
        return suspendCoroutine { cont ->
            if (participantId != null) {
                FirebaseFirestore.getInstance().collection("users")
                    .document(participantId)
                    .get()
                    .addOnSuccessListener {
                        val name = it.get(Constants.NAME).toString()
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

    fun readChats(): Flow<MutableList<String>> {
        return callbackFlow {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val list = mutableListOf<Chat>()
            val participantList = mutableListOf<String>()
            if (uid != null) {
                val ref = FirebaseFirestore.getInstance().collection("chats")
                    .whereArrayContains(Constants.COLUMN_PARTICIPANTS, uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            cancel("error fetching collection data at path", error)
                        }
                        if (snapshot != null) {
                            for (document in snapshot.documentChanges) {
                                if (document.type == DocumentChange.Type.ADDED) {
                                    val list1 =
                                        document.document.get(Constants.COLUMN_PARTICIPANTS) as MutableList<String>
                                    Log.d("participants", list1.toString())
                                    list1.remove(uid)
                                    participantList.add(list1[0])
                                }
                            }
                            offer(participantList)
                        }
                    }
                awaitClose {
                    ref.remove()
                }
            }
        }
    }

    suspend fun readAllUsers(): MutableList<UserIDToken> {
        return suspendCoroutine { cont ->
            val userList = mutableListOf<UserIDToken>()
            FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnSuccessListener {
                    for (i in it.documents) {
                        val id = i.id
                        val name = i.get(Constants.NAME).toString()
                        val status = i.get(Constants.COLUMN_STATUS).toString()
                        val token = i.get(Constants.DEVICE_TOKEN).toString()
                        val uri = i.get(Constants.COLUMN_URI).toString()
                        val user =
                            UserIDToken(
                                uid = id,
                                name = name,
                                status = status,
                                image = uri,
                                token = token
                            )
                        Log.d("users", user.toString())
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
                                    messageType = msg.getString(Constants.MESSAGE_TYPE)!!,
                                    imageUri = msg.getString(Constants.COLUMN_IMAGE_URI)!!
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

    suspend fun addNewMessage(receiver: String?, message: String, type: String): Boolean {
        return suspendCoroutine { cont ->
            val sender = FirebaseAuth.getInstance().currentUser?.uid
            val time = System.currentTimeMillis()
            if (sender != null && receiver != null) {
                val key = getDocumentKey(receiver, sender)
                var imageUri: String
                var textMessage: String

                if (type == Constants.MESSAGE_TYPE_IMAGE) {
                    imageUri = message
                    textMessage = ""
                } else {
                    textMessage = message
                    imageUri = ""
                }
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
                                    message = textMessage,
                                    messageType = type,
                                    imageUri = imageUri,
                                    sentTime = time
                                )
                                it.reference.set(chat)
                                    .addOnSuccessListener {
                                        cont.resumeWith(Result.success(true))
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
                    .limitToLast(15)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            cancel("error fetching collection data at path", error)
                        }
                        if (snapshot != null) {
                            for (document in snapshot.documentChanges) {
                                if (document.type == DocumentChange.Type.ADDED) {
                                    Log.d("snapshot", "inside snapshot")
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
                                    val imageUri = document.document.get(Constants.COLUMN_IMAGE_URI)
                                        .toString()
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
                                        imageUri,
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

    fun getGroups(): Flow<MutableList<GroupDetails>> {
        return callbackFlow {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val grpList = mutableListOf<GroupDetails>()
            if (userId != null) {
                val ref = FirebaseFirestore.getInstance().collection(Constants.GROUP_CHAT)
                    .whereArrayContains(Constants.COLUMN_PARTICIPANTS, userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            cancel("error fetching collection data at path", error)
                        }
                        if (snapshot != null) {
                            for (document in snapshot.documentChanges) {
                                if (document.type == DocumentChange.Type.ADDED) {
                                    val id = document.document.id
                                    val groupName = document.document.get(Constants.NAME).toString()
                                    val grp = GroupDetails(id, groupName)
                                    grpList.add(grp)
                                }

                            }
                            offer(grpList)
                        }
                    }
                awaitClose {
                    ref.remove()
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
                    .limitToLast(10)
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
                                    val imageUri = document.document.get(Constants.COLUMN_IMAGE_URI)
                                        .toString()
                                    val senderName =
                                        document.document.get(Constants.COLUMN_SENDER_NAME)
                                            .toString()
                                    val chat = GroupChat(
                                        messageId = messageid,
                                        senderId = senderId,
                                        senderName = senderName,
                                        message = message,
                                        messageType = messageType,
                                        sentTime = sentTime,
                                        imageUri = imageUri
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

    suspend fun addnewgrpMessage(
        groupId: String?,
        user: User,
        message: String,
        type: String
    ): Boolean {
        return suspendCoroutine { cont ->
            val time = System.currentTimeMillis()
            val senderId = FirebaseAuth.getInstance().currentUser?.uid
            var textMessage: String
            var imageuri: String
            if (type == Constants.MESSAGE_TYPE_TEXT) {
                textMessage = message
                imageuri = ""
            } else {
                imageuri = message
                textMessage = ""
            }
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
                                    message = textMessage,
                                    messageId = messageId,
                                    messageType = type,
                                    senderId = senderId,
                                    senderName = user.userName,
                                    sentTime = time,
                                    imageUri = imageuri
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

    fun getAllUsersFromDb(): Flow<ArrayList<UserIDToken>?> {
        return callbackFlow {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val userList = ArrayList<UserIDToken>()
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
                                        val token = item.get(Constants.DEVICE_TOKEN).toString()
                                        val name = item.get(Constants.NAME).toString()
                                        val status = item.get(Constants.COLUMN_STATUS).toString()
                                        val uri = item.get(Constants.COLUMN_URI).toString()
                                        val user = UserIDToken(id, name, status, uri, token)
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

    suspend fun createGrp(name: String, list: ArrayList<String>?): Boolean {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null && list != null) {
                list.add(uid)
                val group = CreateGroup(name, list)
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

    suspend fun loadNextChats(receiver: String?, offset: Long): MutableList<Chats> {
        return suspendCoroutine { cont ->
            val sender = FirebaseAuth.getInstance().currentUser?.uid
            val list = mutableListOf<Chats>()
            if (offset != 0L && sender != null && receiver != null) {
                Log.d("pagination", "offset $offset")
                val documentKey = getDocumentKey(receiver, sender)
                FirebaseFirestore.getInstance().collection("chats")
                    .document(documentKey)
                    .collection(Constants.MESSAGES)
                    .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                    .startAfter(offset)
                    .limit(10)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val querySnapshot = it.result
                            if (querySnapshot != null) {
                                for (i in querySnapshot.documents) {
                                    val messageid =
                                        i.get(Constants.COLUMN_MESSAGE_ID)
                                            .toString()
                                    val senderId =
                                        i.get(Constants.COLUMN_SENDER_ID).toString()
                                    val receiverId =
                                        i.get(Constants.COLUMN_RECEIVER_ID)
                                            .toString()
                                    val message =
                                        i.get(Constants.COLUMN_MESSAGE).toString()
                                    Log.d("pagination", "querysnapshot size $message")
                                    val imageUri = i.get(Constants.COLUMN_IMAGE_URI)
                                        .toString()
                                    val messageType =
                                        i.get(Constants.COLUMN_MESSAGE_TYPE)
                                            .toString()
                                    val sentTime =
                                        i.get(Constants.SENT_TIME).toString()
                                            .toLong()
                                    val chat = Chats(
                                        messageid,
                                        senderId,
                                        receiverId,
                                        message,
                                        messageType,
                                        imageUri,
                                        sentTime
                                    )
                                    list.add(chat)
                                }
                            }
                            cont.resumeWith(Result.success(list))
                        } else {
                            Log.d("pagination", "failed")
                        }
                    }
                    .addOnFailureListener {
                        Log.d("pagination", "failed")
                    }
            }
        }
    }

    suspend fun loadNextGroupChats(grpId: String?, offset: Long): MutableList<GroupChat> {
        return suspendCoroutine { cont ->
            val list = mutableListOf<GroupChat>()
            if (offset != 0L && grpId != null) {
                Log.d("pagination", "offset $offset")
                FirebaseFirestore.getInstance().collection(Constants.GROUP_CHAT)
                    .document(grpId)
                    .collection(Constants.MESSAGES)
                    .orderBy(Constants.SENT_TIME, Query.Direction.DESCENDING)
                    .startAfter(offset)
                    .limit(10)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val querySnapshot = it.result
                            if (querySnapshot != null) {
                                for (i in querySnapshot.documents) {
                                    val messageid =
                                        i.get(Constants.COLUMN_MESSAGE_ID)
                                            .toString()
                                    val senderId =
                                        i.get(Constants.COLUMN_SENDER_ID).toString()
                                    val message =
                                        i.get(Constants.COLUMN_MESSAGE).toString()
                                    val messageType =
                                        i.get(Constants.COLUMN_MESSAGE_TYPE)
                                            .toString()
                                    val sentTime =
                                        i.get(Constants.SENT_TIME).toString()
                                            .toLong()
                                    val imageUri = i.get(Constants.COLUMN_IMAGE_URI)
                                        .toString()
                                    val senderName =
                                        i.get(Constants.COLUMN_SENDER_NAME)
                                            .toString()
                                    val chat = GroupChat(
                                        messageId = messageid,
                                        senderId = senderId,
                                        senderName = senderName,
                                        message = message,
                                        messageType = messageType,
                                        sentTime = sentTime,
                                        imageUri = imageUri
                                    )
                                    list.add(chat)
                                }
                            }
                            cont.resumeWith(Result.success(list))
                        } else {
                            Log.d("pagination", "failed")
                        }
                    }
                    .addOnFailureListener {
                        Log.d("pagination", "failed")
                    }
            }
        }
    }

    suspend fun updateDeviceToken(token: String?, user: User): Boolean {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null && token != null) {
                val userToken = UserWithToken(user.userName, user.status, user.uri, token)
                FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .set(userToken)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            cont.resumeWith(Result.success(true))
                        } else {
                            cont.resumeWith(Result.success(false))
                        }
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }

    suspend fun getGrpParticipantDetails(groupId: String?): MutableList<String> {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null && groupId != null) {
                FirebaseFirestore.getInstance().collection(Constants.GROUP_CHAT)
                    .document(groupId)
                    .get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            val participantList =
                                it.get(Constants.COLUMN_PARTICIPANTS) as MutableList<String>
                            cont.resumeWith(Result.success(participantList))
                        }
                    }
            }
        }
    }

    suspend fun getAllUsers(): MutableList<UserIDToken> {
        return suspendCoroutine { cont ->
            val list = mutableListOf<UserIDToken>()
            FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnSuccessListener {
                    for (i in it.documents) {
                        val id = i.id
                        val name = i.get(Constants.NAME).toString()
                        val status = i.get(Constants.COLUMN_STATUS).toString()
                        val image = i.get(Constants.COLUMN_URI).toString()
                        val token = i.get(Constants.DEVICE_TOKEN).toString()
                        val user = UserIDToken(id, name, status, image, token)
                        list.add(user)
                    }
                    cont.resumeWith(Result.success(list))
                }
        }
    }
}


