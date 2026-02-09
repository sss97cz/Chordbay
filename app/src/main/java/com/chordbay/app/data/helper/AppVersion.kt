package com.chordbay.app.data.helper

import android.content.Context

class AppVersion(context: Context){
    val versionCode: Long
    val versionName: String

    init {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        versionCode = packageInfo.longVersionCode
        versionName = packageInfo.versionName.toString()
    }
}