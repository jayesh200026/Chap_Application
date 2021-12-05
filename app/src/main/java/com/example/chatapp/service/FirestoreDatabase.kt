package com.example.chatapp.service

import com.example.chatapp.service.model.User
import com.example.chatapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.suspendCoroutine

object FirestoreDatabase {
    suspend fun addUserDetails(user: User): Boolean {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
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
                        val user = User(userName = name,status = status)
                        if (user != null) {
                            cont.resumeWith(Result.success(user))
                        }
                    }
                    .addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }
}