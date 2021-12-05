package com.example.chatapp.service

import android.net.Uri
import com.example.chatapp.service.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseService {
    suspend fun addUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val firestoreStatus = FirestoreDatabase.addUserDetails(user)
                firestoreStatus
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun uploadProfilephoto(it: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                FireBaseStorage.uploadprofile(it)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun fetchProfile(): Uri? {
       return withContext(Dispatchers.IO){
           try {
               val uri = FireBaseStorage.fetchProfile()
               uri
           }catch (e: Exception){
               e.printStackTrace()
               null
           }
       }
    }

   suspend fun getUserDetails(): User? {
        return withContext(Dispatchers.IO){
            try {
                FirestoreDatabase.getUserdetails()
            }catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }
}