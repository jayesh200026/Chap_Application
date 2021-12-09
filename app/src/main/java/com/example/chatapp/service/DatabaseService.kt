package com.example.chatapp.service

import android.net.Uri
import android.util.Log
import com.example.chatapp.service.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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

    suspend fun addNewMessage(receiver: String?, message: String) {
        withContext(Dispatchers.IO){
            FirestoreDatabase.addNewMessage(receiver,message)
        }

    }

    suspend fun subcribe(participant: String?): Chats? {
        return withContext(Dispatchers.IO){
            try{
                FirestoreDatabase.subscribeToListener(participant).collect {
                    Log.d("chats",it.toString())
                    it
                }
                null
            }catch (e: Exception){
                null
            }

        }

    }

    suspend fun getGroups(): MutableList<GroupDetails> {
       return withContext(Dispatchers.IO){
           try {
               FirestoreDatabase.getGroups()
           }catch (e: Exception){
               e.printStackTrace()
               mutableListOf<GroupDetails>()
           }
       }
    }

    suspend fun addnewGrpMessage(groupId: String?, message: String) {
        return withContext(Dispatchers.IO){
            FirestoreDatabase.addnewgrpMessage(groupId,message)
        }
    }

    fun getUserListFromDb(): Flow<ArrayList<UserWithID>?> {
        return FirestoreDatabase.getAllUsersFromDb()
    }

    suspend fun createGrp(name: String,list: ArrayList<String>?): Boolean {
        return withContext(Dispatchers.IO){
            try {
                FirestoreDatabase.createGrp(name, list)
            }catch (e: Exception){
                e.printStackTrace()
                false
            }
        }

    }
}