package com.xiaogang.greenplayer.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.a_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)
        simple_video.setOnClickListener {
            val intent = Intent(this, SimpleVideoActivity::class.java)
            startActivity(intent)
        }
        viewpager_video.setOnClickListener {
            val intent = Intent(this, ViewPagerVideoActivity::class.java)
            startActivity(intent)
        }

    }

}
