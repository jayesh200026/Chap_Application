package com.example.chatapp.service

import android.net.Uri
import android.util.Log
import com.example.chatapp.service.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

object DatabaseService {
    suspend fun addUser(user: User,token: String?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val firestoreStatus = FirestoreDatabase.addUserDetails(user,token)
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

    suspend fun readChats(): MutableList<UserIDToken>{
        return withContext(Dispatchers.IO){
            val friends = mutableListOf<UserIDToken>()
            val list = FirestoreDatabase.readChats()
            val allUsers = FirestoreDatabase.readAllUsers()
            for(i in allUsers){
                if(i.uid in list){
                    val chatUser = UserIDToken(i.uid,i.name,i.status,i.image,i.token)
                    friends.add(chatUser)
                }
            }
            friends
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

    suspend fun addNewMessage(receiver: String?, message: String,type: String): Boolean {
        return withContext(Dispatchers.IO){
            try {
                FirestoreDatabase.addNewMessage(receiver, message, type)
            }catch (e: Exception){
                e.printStackTrace()
                false
            }
        }

    }

//    suspend fun subcribe(participant: String?): Chats? {
//        return withContext(Dispatchers.IO){
//            try{
//                FirestoreDatabase.subscribeToListener(participant).collect {
//                    Log.d("chats",it.toString())
//                    it
//                }
//                null
//            }catch (e: Exception){
//                null
//            }
//
//        }
//
//    }

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

    suspend fun addnewGrpMessage(groupId: String?, message: String,type: String):Boolean {
        return withContext(Dispatchers.IO){
            val user = FirestoreDatabase.getUserdetails()
            FirestoreDatabase.addnewgrpMessage(groupId,user,message,type)
        }
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

    suspend fun uploadMessageImage(selectedImagePath: Uri?):Uri? {
        return withContext(Dispatchers.IO){
            val time = System.currentTimeMillis().toString()
            try {
                FireBaseStorage.uploadMessageimages(selectedImagePath, time)
            }
            catch (e: Exception){
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getParticipantDetails(participantId: String?): User? {
            return  withContext(Dispatchers.IO){
                try {
                    FirestoreDatabase.getParticipantDetails(participantId)
                }catch (e: Exception){
                    e.printStackTrace()
                    null
                }
            }
    }

    suspend fun updateDeviceToken(token: String?): Boolean{
        return withContext(Dispatchers.IO) {
           val user = FirestoreDatabase.getUserdetails()
            FirestoreDatabase.updateDeviceToken(token,user)
        }
    }

    suspend fun getGrpParticipantsDetails(groupId: String?):MutableList<UserIDToken> {
        return withContext(Dispatchers.IO){
            val list = mutableListOf<UserIDToken>()
            val participantList = FirestoreDatabase.getGrpParticipantDetails(groupId)
            val allUsers = FirestoreDatabase.getAllUsers()
            for(i in allUsers){
                if(i.uid in participantList){
                    list.add(UserIDToken(i.uid,i.name,i.status,i.image,i.token))
                }
            }
            Log.d("size",list.size.toString())
            list
        }

    }

}