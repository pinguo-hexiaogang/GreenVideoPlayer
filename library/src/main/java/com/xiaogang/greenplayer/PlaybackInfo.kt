package com.xiaogang.greenplayer

class PlaybackInfo {
    @Volatile
    var state: Int = Player.STATE_IDLE
    @Volatile
    var bufferedPosition: Long = 0
    @Volatile
    var playWhenReady: Boolean = false
}