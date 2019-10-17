package com.xiaogang.greenplayer.demo

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.a_viewpager_video.*
import java.io.File

class ViewPagerVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_viewpager_video)
        view_pager.adapter = VideoAdapter(supportFragmentManager)
    }

    class VideoAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private fun getVideoUriByPos(pos: Int): Uri {
            return when (pos) {
                0 -> Uri.parse("https://movie.baobeicang.com/dkzl.mp4")
                1 -> Uri.fromFile(File("/sdcard/testVideo/beauty_video.mp4"))
                2 -> Uri.fromFile(File("/sdcard/testVideo/land_video.mp4"))
                3 -> Uri.parse("https://movie.baobeicang.com/dkzl.mp4")
                else -> Uri.fromFile(File("/sdcard/testVideo/land_video.mp4"))
            }
        }

        override fun getItem(p: Int): Fragment {
            val fragment = VideoFragment()
            val arguments = Bundle()
            arguments?.putParcelable(VideoFragment.KEY_URI, getVideoUriByPos(p))
            fragment.arguments = arguments
            return fragment
        }

        override fun getCount(): Int {
            return 4
        }

    }

}