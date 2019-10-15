package com.xiaogang.greenplayer.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

object Util {
    fun findActivity(ctx: Context): Activity? {
        var context = ctx
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }
}