package com.xiaogang.greenplayer.player

import android.content.Context
import android.graphics.SurfaceTexture
import android.net.Uri
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import com.xiaogang.greenplayer.*
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.concurrent.CopyOnWriteArrayList

class IjkPlayer(private val appContext: Context) : Player, PlayerAudioFocusManager.FocusLostListener {


    private val ijkMediaPlayer: IjkMediaPlayer = IjkMediaPlayer()
    private val ijkListener = IjkListener()
    private var playbackInfo = PlaybackInfo()
    private var surfaceView: SurfaceView? = null
    private var textureView: TextureView? = null
    private var surfaceListener = SurfaceListener()
    @Volatile
    private var uri: Uri? = null
    private var videoListeners = CopyOnWriteArrayList<VideoListener>()
    private var eventListeners = CopyOnWriteArrayList<EventListener>()
    private val audioFocusManager = PlayerAudioFocusManager(appContext, this)

    init {
        ijkMediaPlayer.setOnPreparedListener(ijkListener)
        ijkMediaPlayer.setOnErrorListener(ijkListener)
        ijkMediaPlayer.setOnCompletionListener(ijkListener)
        ijkMediaPlayer.setOnInfoListener(ijkListener)
        ijkMediaPlayer.setOnBufferingUpdateListener(ijkListener)
        ijkMediaPlayer.setOnVideoSizeChangedListener(ijkListener)
        ijkMediaPlayer.setOnSeekCompleteListener(ijkListener)
    }

    override fun setSurface(surface: Surface) {
        surfaceView?.holder?.removeCallback(surfaceListener)
        textureView?.surfaceTextureListener = null
        surfaceView = null
        textureView = null
        ijkMediaPlayer.setSurface(surface)
    }

    override fun setSurfaceView(surfaceView: SurfaceView) {
        this.surfaceView = surfaceView
        surfaceView.holder.addCallback(surfaceListener)
        textureView?.surfaceTextureListener = null
        textureView = null
        ijkMediaPlayer.setSurface(surfaceView.holder.surface)
    }

    override fun setTextureView(textureView: TextureView) {
        this.textureView = textureView
        textureView.surfaceTextureListener = surfaceListener
        surfaceView?.holder?.removeCallback(surfaceListener)
        surfaceView = null
        if (textureView.isAvailable) {
            ijkMediaPlayer.setSurface(Surface(textureView.surfaceTexture))
        } else {
            ijkMediaPlayer.setSurface(null)
        }
    }

    override fun clearVideoSurface() {
        ijkMediaPlayer.setSurface(null)
    }

    override fun clearVideoSurfaceView(surfaceView: SurfaceView) {
        if (surfaceView == this.surfaceView) {
            surfaceView.holder.removeCallback(surfaceListener)
            ijkMediaPlayer.setSurface(null)
        }
    }

    override fun clearVideoTextureView(textureView: TextureView) {
        if (textureView == this.textureView) {
            textureView.surfaceTextureListener = null
            ijkMediaPlayer.setSurface(null)
        }
    }

    override fun addVideoListener(videoListener: VideoListener) {
        if (!videoListeners.contains(videoListener)) {
            videoListeners.add(videoListener)
        }
    }

    override fun removeVideoListener(videoListener: VideoListener) {
        videoListeners.remove(videoListener)
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        playbackInfo.playWhenReady = playWhenReady
        if (playbackInfo.state == Player.STATE_READY || playbackInfo.state == Player.STATE_BUFFING) {
            if (playbackInfo.playWhenReady) {
                if (audioFocusManager.requestFocus()) {
                    ijkMediaPlayer.start()
                } else {
                    playbackInfo.playWhenReady = false
                }
            } else {
                ijkMediaPlayer.pause()
                audioFocusManager.abadonFoucs()
            }
        } else if (playbackInfo.state == Player.STATE_END) {
            if (playWhenReady) {
                if (audioFocusManager.requestFocus()) {
                    ijkMediaPlayer.start()
                } else {
                    playbackInfo.playWhenReady = false
                }
            }
        } else {
            if (uri != null && playWhenReady) {
                ijkMediaPlayer.prepareAsync()
                playbackInfo.state = Player.STATE_BUFFING
            }
        }
        dispatchStateChange()
    }

    override fun getPlayWhenReady(): Boolean {
        return playbackInfo.playWhenReady
    }

    override fun getPlaybackState(): Int {
        return playbackInfo.state
    }

    override fun getCurrentPosition(): Long {
        return ijkMediaPlayer.currentPosition
    }

    override fun getDuration(): Long {
        return ijkMediaPlayer.duration
    }

    override fun getBufferedPosition(): Long {
        return playbackInfo.bufferedPosition
    }

    override fun addEventListener(listener: EventListener) {
        if (!eventListeners.contains(listener)) {
            eventListeners.add(listener)
        }
    }

    override fun removeEventListener(listener: EventListener) {
        eventListeners.remove(listener)
    }

    override fun seekTo(positionMs: Long) {
        ijkMediaPlayer.seekTo(positionMs)
    }

    override fun stop() {
        playbackInfo.state = Player.STATE_IDLE
        ijkMediaPlayer.stop()
        dispatchStateChange()
    }

    override fun release() {
        playbackInfo.state = Player.STATE_IDLE
        dispatchStateChange()
        surfaceView?.holder?.removeCallback(surfaceListener)
        textureView?.surfaceTextureListener = null
        surfaceView = null
        textureView = null
        eventListeners.clear()
        videoListeners.clear()
        audioFocusManager.abadonFoucs()
        ijkMediaPlayer.setSurface(null)
        ijkMediaPlayer.release()
    }

    override fun setSource(context: Context, uri: Uri) {
        if (this.uri != uri) {
            this.uri = uri
            ijkMediaPlayer.setDataSource(context, uri)
            if (playbackInfo.playWhenReady) {
                ijkMediaPlayer.prepareAsync()
            }
            playbackInfo.state = Player.STATE_IDLE
            dispatchStateChange()
        }
    }

    private inner class IjkListener : IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnInfoListener {
        override fun onPrepared(p0: IMediaPlayer?) {
            playbackInfo.state = Player.STATE_READY
            if (playbackInfo.playWhenReady) {
                if (audioFocusManager.requestFocus()) {
                    ijkMediaPlayer.start()
                } else {
                    playbackInfo.playWhenReady = false
                }
            }
            dispatchStateChange()
        }

        override fun onBufferingUpdate(p0: IMediaPlayer?, percent: Int) {
            playbackInfo.bufferedPosition =
                ((percent * 1f / 100) * ijkMediaPlayer.duration).toLong()
        }

        override fun onCompletion(p0: IMediaPlayer?) {
            playbackInfo.state = Player.STATE_END
            playbackInfo.playWhenReady = false
            dispatchStateChange()
        }

        override fun onError(p0: IMediaPlayer?, what: Int, extra: Int): Boolean {
            playbackInfo.state = Player.STATE_END
            dispatchStateChange()
            dispatchOnError(IllegalArgumentException("video error"))
            return false
        }

        override fun onVideoSizeChanged(
            p0: IMediaPlayer?,
            width: Int,
            height: Int,
            sar_num: Int,
            sar_den: Int
        ) {
            dispatchVideoSizeChange(width, height)
        }

        override fun onSeekComplete(p0: IMediaPlayer?) {
            //TODO
        }

        override fun onInfo(p0: IMediaPlayer?, what: Int, extra: Int): Boolean {
            when (what) {
                IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    playbackInfo.state = Player.STATE_BUFFING
                    dispatchStateChange()
                }
                IMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    playbackInfo.state = Player.STATE_READY
                    dispatchStateChange()
                }
                else -> {

                }
            }
            return true
        }

    }

    private inner class SurfaceListener : SurfaceHolder.Callback,
        TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            ijkMediaPlayer.setSurface(null)
            return true
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            ijkMediaPlayer.setSurface(Surface(surface))
        }

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            ijkMediaPlayer.setSurface(null)
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            ijkMediaPlayer.setSurface(holder?.surface)
        }

    }

    private fun dispatchStateChange() {
        eventListeners.forEach {
            it.onPlayerStateChange(playbackInfo.state, playbackInfo.playWhenReady)
        }
    }

    private fun dispatchVideoSizeChange(width: Int, height: Int) {
        videoListeners.forEach {
            it.onVideoSizeChagne(width, height)
        }
    }

    private fun dispatchOnError(e: Exception) {
        eventListeners.forEach {
            it.onPlayError(e)
        }
    }

    override fun videoWidth(): Int {
        return ijkMediaPlayer.videoWidth
    }

    override fun videoHeight(): Int {
        return ijkMediaPlayer.videoHeight
    }

    override fun onFocusLost() {
        setPlayWhenReady(false)
    }
}