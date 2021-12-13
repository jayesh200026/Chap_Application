package com.example.chatapp.ui.network

import android.provider.SyncStateContract
import com.example.chatapp.ui.network.PushNotificationApi.Companion.BASE_URL
import com.example.chatapp.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class PushNotificationSenderService {

    suspend fun sendPushNotification(message: PushMessage): PushResponse {
        val api = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create<PushNotificationApi>()

        return  api.sendPushNotification(
            Constants.ACCESS_TOKEN,
            message
        )
    }
}