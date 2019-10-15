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
        private var player:Player? = null
        fun startFull(context: Context, playerView: PlayerView?) {
            this.playerView = playerView
            this.player = playerView?.getPlayer()
            //这里要首先设置为null，否则全屏会导致界面生命周期变化，导致视频暂停之类的问题。
            this.playerView?.setPlayer(null)
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
            player_view.setPlayer(player)
        }
        if (!isVerticalVideo()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    override fun onPause() {
        super.onPause()
        if(!isFinishing) {
            player_view.getPlayer()?.setPlayWhenReady(false)
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