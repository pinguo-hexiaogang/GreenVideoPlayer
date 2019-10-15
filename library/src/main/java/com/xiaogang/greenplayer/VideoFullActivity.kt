package com.xiaogang.greenplayer

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.green_full_layout.*

class VideoFullActivity : AppCompatActivity() {
    companion object {
        private var playerView: PlayerView? = null
        fun startFull(context: Context, playerView: PlayerView?) {
            this.playerView = playerView
            val intent = Intent(context, VideoFullActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.green_full_layout)
        player_view.inFullState = true
        if (playerView == null) {
            finish()
        } else {
            PlayerView.switchTarget(playerView, player_view)
        }
        if (!isVerticalVideo()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun isVerticalVideo(): Boolean {
        val videoWidth = player_view?.getPlayer()?.videoWidth() ?: 0
        val videoHeight = player_view?.getPlayer()?.videoHeight() ?: 0
        return if (videoWidth <= 0 || videoHeight <= 0) {
            true
        } else {
            videoWidth <= videoHeight
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerView.switchTarget(player_view, playerView)
        playerView = null
    }
}