package com.xiaogang.greenplayer

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet

abstract class BaseController @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    abstract fun setPlayerView(playerView: PlayerView)

}