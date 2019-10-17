package com.xiaogang.greenplayer.demo

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xiaogang.greenplayer.Player
import com.xiaogang.greenplayer.PlayerView
import com.xiaogang.greenplayer.player.IjkPlayer
import com.xiaogang.greenplayer.utils.LogDebug

class VideoFragment : Fragment() {
    companion object {
        const val KEY_URI = "uri"
    }

    private var playerView: PlayerView? = null
    private var currentPosition: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val playerView = PlayerView(context!!)
        playerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        playerView.setPlayer(createPlayer())
        this.playerView = playerView

        return playerView
    }

    private fun createPlayer(): Player {
        val uri = arguments?.getParcelable(KEY_URI) as? Uri
        val player = IjkPlayer(context!!.applicationContext)
        if (uri != null) {
            player.setSource(context!!, uri)
        }
        return player
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser) {
            currentPosition = playerView?.getPlayer()?.getCurrentPosition() ?: 0
            playerView?.release()
        } else if (playerView != null && playerView?.getPlayer() == null) {
            playerView?.setPlayer(createPlayer())
            LogDebug.d("VideoFragment", "seek to pos:$currentPosition")
            playerView?.getPlayer()?.seekTo(currentPosition)
        }
    }

    override fun onPause() {
        super.onPause()
        playerView?.getPlayer()?.setPlayWhenReady(false)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}