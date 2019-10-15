package com.xiaogang.greenplayer

import android.content.Context
import android.media.AudioManager

class PlayerAudioFocusManager(
    private val context: Context,
    private val onFocusLostListener: FocusLostListener
) : AudioManager.OnAudioFocusChangeListener {
    private val audioManager =
        context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun requestFocus(): Boolean {
        return audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abadonFoucs() {
        audioManager.abandonAudioFocus(this)

    }

    interface FocusLostListener {
        fun onFocusLost()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                onFocusLostListener.onFocusLost()
            }
        }
    }
}