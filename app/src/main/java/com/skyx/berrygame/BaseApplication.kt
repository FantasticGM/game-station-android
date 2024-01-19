package com.skyx.berrygame

import android.app.Application
import com.skyx.berrygame.utils.LanguageHelper

class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        LanguageHelper.getAttachBaseContext(this)
    }
}