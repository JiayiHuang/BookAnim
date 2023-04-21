package com.example.hello

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityOptionsCompat

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val durationAnim = 1500L
    private val mTv: TextView by lazy { findViewById(R.id.main_tv) }
    private val mIvSnapshot: ImageView by lazy { findViewById(R.id.main_iv_snapshot) }
    private val mTvCover: TextView by lazy { findViewById(R.id.main_tv_cover) }
    private val mRoot: FrameLayout by lazy { findViewById(R.id.main_root_fl) }
    private val mTvGo3rd: TextView by lazy { findViewById(R.id.main_tv_3rd) }
    private lateinit var mTvPage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTv.setOnClickListener {
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, mTv, "transitionName"
            ).toBundle()
            startActivity(Intent(this, SecondActivity::class.java), bundle);
        }
        mTvGo3rd.setOnClickListener {
            start3rdActivity(this)
        }

        mTvCover.setOnClickListener {
            animOpen()
        }
    }

    private fun animOpen() {
        mTvPage = BookAnimUtil.inst.createPageView(this).apply {
            visibility = View.INVISIBLE
        }
        val params = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        mRoot.addView(mTvPage, mRoot.indexOfChild(mTvCover), params)
        val startViewInfo = Quadruple(
            mTvCover.left * 1f, mTvCover.top * 1f, mTvCover.width * 1f, mTvCover.height * 1f
        )
        BookAnimUtil.inst.setAnimStartViews(mRoot, mTvCover, mTvPage, mIvSnapshot, startViewInfo)

        mTvPage.post {
            AnimatorSet().apply {
                duration = durationAnim
                playTogether(BookAnimUtil.inst.coverAnim(true).apply {
                    addAll(BookAnimUtil.inst.bgAnim(true))
                })
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        mIvSnapshot.visibility = View.GONE
                        mTvPage.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        mIvSnapshot.visibility = View.VISIBLE
                        mIvSnapshot.setImageBitmap(BookAnimUtil.createBitmap(mTvPage))
                        mTvCover.visibility = View.INVISIBLE
                        mTvPage.visibility = View.INVISIBLE
                        showCloseAnim = true
                        start2ndActivity(this@MainActivity)
                        overridePendingTransition(0, 0)
                    }
                })
            }.start()
        }
    }

    private fun animClose() {
        mTvPage = BookAnimUtil.inst.getPageView(this)
        val params = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        mRoot.addView(mTvPage, mRoot.indexOfChild(mTvCover), params)
        mTvPage.post {
            AnimatorSet().apply {
                playTogether(BookAnimUtil.inst.coverAnim(false).apply {
                    addAll(BookAnimUtil.inst.bgAnim(false))
                })
                duration = durationAnim
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        mIvSnapshot.visibility = View.GONE
                        mTvCover.visibility = View.VISIBLE
                        mTvPage.visibility = View.VISIBLE
                    }
                })
            }.start()
        }
    }

    var showCloseAnim = false
    override fun onResume() {
        super.onResume()
        if (showCloseAnim) {
            animClose()
        }
        showCloseAnim = false
    }
}