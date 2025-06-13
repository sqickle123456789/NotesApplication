package com.sqickle.spacenotes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpaceApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}