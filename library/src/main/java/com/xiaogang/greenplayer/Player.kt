package com.xiaogang.greenplayer

import android.content.Context
import android.net.Uri
import android.view.Surface
import android.view.SurfaceView
import android.view.TextureView

interface Player {
    companion object {
        /**
         *还没设置播放数据
         */
        const val STATE_IDLE = 1
        /**
         * 正在缓存数据
         */
        const val STATE_BUFFING = 2
        /**
         * 随时可以播放了
         */
        const val STATE_READY = 3
        /**
         * 播放完成
         */
        const val STATE_END = 4
    }


    fun setSurface(surface: Surface)
    fun setSurfaceView(surfaceView: SurfaceView)
    fun setTextureView(textureView: TextureView)
    fun clearVideoSurface()
    fun clearVideoSurfaceView(surfaceView: SurfaceView)
    fun clearVideoTextureView(textureView: TextureView)
    fun addVideoListener(videoListener: VideoListener)
    fun removeVideoListener(videoListener: VideoListener)

    fun setPlayWhenReady(playWhenReady: Boolean)

    fun getPlayWhenReady(): Boolean

    /**
     * 返回播放状态[STATE_BUFFING]等
     */
    fun getPlaybackState(): Int

    /**
     * 返回当前播放进度,以ms为单位
     */
    fun getCurrentPosition(): Long

    /**
     * 返回播放时长，以ms为单位
     */
    fun getDuration(): Long

    /**
     * 返回已经缓存的进度，以ms为单位
     */
    fun getBufferedPosition(): Long

    fun addEventListener(listener: EventListener)

    fun removeEventListener(listener: EventListener)

    fun seekTo(positionMs: Long)

    /**
     * 状态转变为[STATE_IDLE],该palyer还可以使用
     */
    fun stop()

    /**
     * 调用该方法后，该player不能继续使用
     */
    fun release()

    fun setSource(context:Context,uri: Uri)

    fun videoWidth():Int

    fun videoHeight():Int

}