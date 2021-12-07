package com.example.chatapp.service

import android.net.Uri
import com.example.chatapp.service.model.*
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

    suspend fun uploadProfilephoto(it: Uri?): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                FireBaseStorage.uploadprofile(it)
            } catch (e: Exception) {
                e.printStackTrace()
                null
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

    suspend fun readChats(): MutableList<UserWithID>{
        return withContext(Dispatchers.IO){
            val friends = mutableListOf<UserWithID>()
            val list = FirestoreDatabase.readChats()
            val allUsers = FirestoreDatabase.readAllUsers()
            for(i in allUsers){
                if(i.userId in list){
                    friends.add(i)
                }
            }
            friends
        }
    }

    suspend fun readAllUsers():MutableList<UserWithID> {
        return withContext(Dispatchers.IO){
            try {
               val userList = FirestoreDatabase.readAllUsers()
                userList
            }catch (e: Exception){
                e.printStackTrace()
               mutableListOf<UserWithID>()
            }
        }
    }

   suspend fun getAllChats(participant: String?):MutableList<AllMessages> {
        return withContext(Dispatchers.IO){
            try {
                FirestoreDatabase.getAllChats(participant)
            }catch (e: Exception){
                e.printStackTrace()
                mutableListOf<AllMessages>()
            }
        }

    }
}