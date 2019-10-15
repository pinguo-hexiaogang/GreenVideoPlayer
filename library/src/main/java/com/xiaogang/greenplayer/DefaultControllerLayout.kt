package com.xiaogang.greenplayer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.SeekBar
import com.xiaogang.greenplayer.utils.Util
import com.xiaogang.greenplayer.utils.toMiniteSeconds
import kotlinx.android.synthetic.main.green_controller_layout.view.*

open class DefaultControllerLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseController(context, attrs, defStyleAttr), EventListener {
    private val handlerMain = Handler(Looper.getMainLooper())
    private val checkProgressRunnable = CheckProgressRunnable(false)
    private var playerView: PlayerView? = null


    override fun setPlayerView(playerView: PlayerView) {
        this.playerView?.getPlayer()?.removeEventListener(this)
        this.playerView = playerView
        this.playerView?.getPlayer()?.addEventListener(this)
        if (this.playerView?.getPlayer() != null) {
            restoreUiStatus()
        }
        updateFullState()
    }

    /**
     * 根据player，设置UI状态
     */
    private fun restoreUiStatus() {
        //总的时间
        setTotalTime()
        //status button 状态
        val innerPlayer = playerView?.getPlayer()
        setStatusResource(innerPlayer?.getPlayWhenReady() ?: false)
        if (innerPlayer?.getPlayWhenReady() == true
            && innerPlayer?.getPlaybackState() != Player.STATE_IDLE
            && innerPlayer?.getPlaybackState() != Player.STATE_END
        ) {
            startUpdateProgress()
        }
    }


    override fun onFinishInflate() {
        super.onFinishInflate()
        status_imv.setOnClickListener {
            playerView?.getPlayer()?.setPlayWhenReady(!(playerView?.getPlayer()?.getPlayWhenReady() ?: false))
        }
        progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                stopUpdateProgress()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                val duration = playerView?.getPlayer()?.getDuration() ?: 0
                val seekToPos = (duration * (progress * 1.0 / 100)).toLong()
                playerView?.getPlayer()?.seekTo(seekToPos)
                startUpdateProgress()

            }

        })
        fullscreen.setOnClickListener {
            if (playerView?.inFullState == true) {
                Util.findActivity(context)?.finish()
            } else {
                VideoFullActivity.startFull(context, playerView)
            }
        }
        updateFullState()
    }

    private fun updateFullState() {
        val isFullState = playerView?.inFullState ?: false
        val res = if (isFullState) R.drawable.out_full_screen else R.drawable.in_full_screen
        fullscreen.setImageResource(res)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        playerView?.getPlayer()?.removeEventListener(this)
        stopUpdateProgress()
    }

    private fun setStatusResource(changeToPause: Boolean) {
        if (changeToPause) {
            status_imv.setImageResource(R.drawable.video_pause)
        } else {
            status_imv.setImageResource(R.drawable.video_play)
        }
    }

    private fun changeStatusVisibility(visible: Boolean) {
        status_imv.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setTotalTime() {
        val totalTimeInMills = playerView?.getPlayer()?.getDuration() ?: 0
        val totalTimeStr = totalTimeInMills.toMiniteSeconds()
        total?.text = totalTimeStr
    }

    private fun startUpdateProgress() {
        checkProgressRunnable.notPost = false
        handlerMain.post(checkProgressRunnable)
    }

    private fun stopUpdateProgress() {
        checkProgressRunnable.notPost = true
        handlerMain.removeCallbacks(checkProgressRunnable)
    }

    override fun onPlayerStateChange(state: Int, playWhenReady: Boolean) {
        setStatusResource(playWhenReady)
        if (playWhenReady) {
            startUpdateProgress()
        } else {
            stopUpdateProgress()
        }
        when (state) {
            Player.STATE_END -> {
                changeStatusVisibility(true)
                setStatusResource(false)
                stopUpdateProgress()
            }
            Player.STATE_READY -> {
                changeStatusVisibility(true)
                setTotalTime()
            }
            Player.STATE_IDLE -> {
                changeStatusVisibility(true)
            }
            Player.STATE_BUFFING -> {
                //此时需要show buffing
                changeStatusVisibility(false)
            }
        }
    }

    private fun updateProgress() {
        val innerPlayer = playerView?.getPlayer()
        val duration =
            if (innerPlayer?.getDuration() == null || innerPlayer!!.getDuration() == 0L) {
                Long.MAX_VALUE
            } else {
                innerPlayer!!.getDuration()
            }
//        LogDebug.d(
//            "controller",
//            "duration:$duration,current:${innerPlayer?.getCurrentPosition()},buffer:${innerPlayer?.getBufferedPosition()}"
//        )

        progress.progress =
            (((innerPlayer?.getCurrentPosition() ?: 0) * 1.0 / duration) * 100).toInt()
        progress.secondaryProgress =
            (((innerPlayer?.getBufferedPosition() ?: 0) * 1f / duration) * 100).toInt()
        current.text = innerPlayer?.getCurrentPosition().toMiniteSeconds()

    }

    private inner class CheckProgressRunnable(var notPost: Boolean) : Runnable {
        override fun run() {
            updateProgress()
            if (!notPost) {
                handlerMain.postDelayed(checkProgressRunnable, 50)
            }
        }
    }

    override fun onPlayError(e: Exception?) {
        //TODO
    }
}