package com.example.bitfitpart1

import android.app.Application

class ItemApplication : Application() {
    val db by lazy { AppDatabase.getInstance(this) }
}