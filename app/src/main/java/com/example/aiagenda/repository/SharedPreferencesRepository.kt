package com.example.aiagenda.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.aiagenda.model.User
import com.example.aiagenda.util.SharedPrefConstants
import com.google.gson.Gson

class SharedPreferencesRepository(val context: Context) {

    private val appPreferences: SharedPreferences = context.getSharedPreferences(
        SharedPrefConstants.LOCAL_SHARED_PREF, Context.MODE_PRIVATE
    )
    private val gson: Gson = Gson()

    fun putString(user: User) {
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
    }

    fun getSession(): String? {
        return appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
    }

    fun getUser(userStr: String): User {
        return gson.fromJson(userStr, User::class.java)
    }

    fun clear() {
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, null).apply()
    }

}