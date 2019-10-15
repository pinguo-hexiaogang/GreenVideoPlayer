package com.xiaogang.greenplayer.demo

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.xiaogang.greenplayer.player.IjkPlayer
import kotlinx.android.synthetic.main.a_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)
        val player = IjkPlayer(this.applicationContext)
        player.setSource(this, Uri.parse("https://movie.baobeicang.com/dkzl.mp4"))
        //player.setSource(this,Uri.fromFile(File("/sdcard/testVideo/beauty_video.mp4")))
        //player.setSource(this,Uri.fromFile(File("/sdcard/testVideo/land_video.mp4")))
        player_view.setPlayer(player)
    }

    override fun onPause() {
        super.onPause()
        player_view.getPlayer()?.setPlayWhenReady(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        player_view.getPlayer()?.release()
    }
}
