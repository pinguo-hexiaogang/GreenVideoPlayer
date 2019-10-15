package com.xiaogang.greenplayer.utils

import android.util.Log

object LogDebug {
    var debugLogEnable = true
    fun d(tag: String, msg: String) {
        if (debugLogEnable) {
            Log.d(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (debugLogEnable) {
            Log.e(tag, msg)
        }
    }
}