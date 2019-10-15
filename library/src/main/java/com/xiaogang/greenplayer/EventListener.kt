package com.xiaogang.greenplayer

import java.lang.Exception

interface EventListener {
    /**
     * after this method called;method [Player.getPlaybackState] will return the new state
     */
    fun onPlayerStateChange(state: Int, playWhenReady: Boolean)

    /**
     * play error
     */
    fun onPlayError(e: Exception?)
}
