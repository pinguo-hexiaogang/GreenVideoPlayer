package com.xiaogang.greenplayer

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import com.xiaogang.greenplayer.ui.PlayerViewContentLayout
import com.xiaogang.greenplayer.utils.LogDebug
import kotlinx.android.synthetic.main.green_player_view.view.*
import java.lang.Exception

class PlayerView : FrameLayout, VideoListener, EventListener {


    companion object {
        const val SURFACE_TYPE_NONE = 0
        const val SURFACE_TYPE_SURFACE_VIEW = 1
        const val SURFACE_TYPE_TEXTURE_VIEW = 2
        const val TAG = "PlayerView"

        fun switchTarget(from: PlayerView?, to: PlayerView?) {
            val player = from?.innerPlayer
            from?.setPlayer(null)
            to?.setPlayer(player)
        }
    }

    private var surfaceType: Int = SURFACE_TYPE_TEXTURE_VIEW
    private var fillType = PlayerViewContentLayout.FILL_TYPE_FIT
    private var surfaceView: View? = null
    private var innerPlayer: Player? = null
    private var userController: Boolean = true
    private var innerController: BaseController? = null
    /**
     * 是否是全屏模式
     */
    var inFullState: Boolean = false

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attr: AttributeSet?) : this(ctx, attr, 0)
    constructor(ctx: Context, attr: AttributeSet?, defStyle: Int) : super(ctx, attr, defStyle) {
        LayoutInflater.from(ctx).inflate(R.layout.green_player_view, this)
        val resource = ctx.obtainStyledAttributes(attr, R.styleable.PlayerView, defStyle, 0)
        try {
            surfaceType =
                resource.getInt(R.styleable.PlayerView_surfaceType, SURFACE_TYPE_TEXTURE_VIEW)
            userController = resource.getBoolean(R.styleable.PlayerView_useController, true)
            fillType = resource.getInt(
                R.styleable.PlayerView_fillType,
                PlayerViewContentLayout.FILL_TYPE_FIT
            )
        } finally {
            resource.recycle()
        }
        if (userController) {
            val controller = LayoutInflater.from(context)
                .inflate(
                    R.layout.green_controller_layout,
                    controller_container,
                    false
                ) as DefaultControllerLayout
            setController(controller)
        }
        content.fillType = fillType
        surfaceView = addSurface()

    }


    fun useController(use: Boolean) {
        this.userController = use
        if (use) {
            setController(innerController)
        } else {
            removeController()
        }
    }

    private fun removeController() {
        controller_container.removeAllViews()
    }

    fun hideController() {
        innerController?.visibility = View.GONE
    }

    fun showController() {
        innerController?.visibility = View.VISIBLE
    }

    fun setController(controller: BaseController?) {
        this.innerController = controller
        controller?.setPlayerView(this)
        controller_container.removeAllViews()
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        controller_container.addView(innerController, params)
    }

    private fun addSurface(): View? {
        val surfaceView = when (surfaceType) {
            SURFACE_TYPE_SURFACE_VIEW -> SurfaceView(context)
            SURFACE_TYPE_TEXTURE_VIEW -> TextureView(context)
            else -> null
        }
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if (surfaceView != null) {
            content.addView(surfaceView, 0, layoutParams)
        }
        return surfaceView
    }

    fun setPlayer(player: Player?) {
        if (this.innerPlayer != player) {
            innerPlayer?.removeEventListener(this)
            innerPlayer?.removeVideoListener(this)
            if (surfaceView is SurfaceView) {
                innerPlayer?.clearVideoSurfaceView(surfaceView as SurfaceView)
            } else if (surfaceView is TextureView) {
                innerPlayer?.clearVideoTextureView(surfaceView as TextureView)
            }


            innerPlayer = player
            innerPlayer?.addEventListener(this)
            innerPlayer?.addVideoListener(this)
            innerController?.setPlayerView(this)
            if (surfaceView == null && innerPlayer != null) {
                surfaceView = addSurface()
            }
            if (surfaceView is SurfaceView) {
                innerPlayer?.setSurfaceView(surfaceView as SurfaceView)
            } else if (surfaceView is TextureView) {
                LogDebug.d(TAG, "setTextureView")
                innerPlayer?.setTextureView(surfaceView as TextureView)
            }
            if (this.innerPlayer != null && this.innerPlayer?.videoWidth() ?: 0 > 0) {
                val videoWidth = this.innerPlayer?.videoWidth() ?: 0
                val videoHeight = this.innerPlayer?.videoHeight() ?: 1
                content.videoRation = videoWidth * 1f / videoHeight
            }
        }

    }

    /**
     * 调用player的release方法；remove surfaceView
     */
    fun release() {
        innerPlayer?.release()
        setPlayer(null)
        if (surfaceView != null) {
            content.removeView(surfaceView)
        }
        surfaceView = null
    }

    fun getPlayer(): Player? = innerPlayer

    override fun onVideoSizeChagne(width: Int, height: Int) {
        content.videoRation = width * 1f / height
    }

    override fun onPlayerStateChange(state: Int, playWhenReady: Boolean) {
        when (state) {
            Player.STATE_BUFFING -> {
                buffer_progress.visibility = View.VISIBLE
            }
            else -> {
                buffer_progress.visibility = View.GONE
            }
        }
    }

    override fun onPlayError(e: Exception?) {
    }
}