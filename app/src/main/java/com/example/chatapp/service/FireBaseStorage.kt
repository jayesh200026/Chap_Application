package com.example.chatapp.service

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlin.coroutines.suspendCoroutine

object FireBaseStorage {
    suspend fun uploadprofile(profile: Uri?): Uri {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null && profile != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                storageRef.child("users/" + uid + ".jpg")
                    .putFile(profile)
                    .addOnSuccessListener {
                        it.storage.downloadUrl.addOnSuccessListener {
                            cont.resumeWith(Result.success(it))
                        }.addOnFailureListener {
                            cont.resumeWith(Result.failure(it))
                        }
                    }.addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }

    suspend fun fetchProfile(): Uri? {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                val filerRef = storageRef.child("users/" + uid + ".jpg")
                filerRef.downloadUrl.addOnSuccessListener {
                    cont.resumeWith(Result.success(it))
                }
                    .addOnFailureListener {
                        cont.resumeWith(Result.success(null))
                    }
            }
        }
    }

    suspend fun uploadMessageimages(selectedImagePath: Uri?,time: String): Uri {

        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            if (uid != null && selectedImagePath != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                storageRef.child("ImagesMessages/" + time + ".jpg")
                    .putFile(selectedImagePath)
                    .addOnSuccessListener {
                        it.storage.downloadUrl.addOnSuccessListener {
                            cont.resumeWith(Result.success(it))
                        }.addOnFailureListener {
                            cont.resumeWith(Result.failure(it))
                        }
                    }.addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }
}