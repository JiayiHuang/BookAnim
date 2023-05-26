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
    private val mIvCopy: ImageView by lazy { findViewById(R.id.main_iv_copy) }
    private val mCoverView: View by lazy { findViewById(R.id.main_tv_cover) }
    private val mRoot: FrameLayout by lazy { findViewById(R.id.main_root_fl) }
    private val mTvGo3rd: TextView by lazy { findViewById(R.id.main_tv_3rd) }
    private lateinit var mTvPage: TextView
    private val mTvTextList: TextView by lazy { findViewById(R.id.main_tv_test_list) }
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

        mCoverView.setOnClickListener {
            animOpen()
        }

        mTvTextList.setOnClickListener {
            val list = mutableListOf<String>().apply {
                add("1")
                add("12")
                add("123")
                add("1234")
            }
        }
    }

    private fun animOpen() {
        mTvPage = BookAnimUtil.inst.getPageView(this).apply {
            visibility = View.INVISIBLE
        }
        val params = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        mRoot.addView(mTvPage, mRoot.indexOfChild(mCoverView), params)
        val startViewInfo = Quadruple(
            mCoverView.left * 1f, mCoverView.top * 1f, mCoverView.width * 1f, mCoverView.height * 1f
        )
        BookAnimUtil.inst.setAnimStartViews(mRoot, mCoverView, mTvPage, mIvSnapshot, startViewInfo)
        Anim2ndUtil.inst.setAnchorInfo(startViewInfo)

//        mIvCopy.apply {
//            setImageBitmap(BookAnimUtil.inst.genCoverBitmap())
//            layoutParams.width = mCoverView.width
//            layoutParams.height = mCoverView.height
//        }

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
                        mIvSnapshot.setImageBitmap(BookAnimUtil.inst.createBitmap(mTvPage))
                        mCoverView.visibility = View.INVISIBLE
                        mTvPage.visibility = View.INVISIBLE
                        showCloseAnim = true
                        start2ndActivity(this@MainActivity)
                        overridePendingTransition(0, 0)
                    }
                })
            }.apply {
//                start()
            }
        }
    }

    private fun animClose() {
        mTvPage = BookAnimUtil.inst.getPageView(this)
        val params = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        mRoot.addView(mTvPage, mRoot.indexOfChild(mCoverView), params)
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
                        mCoverView.visibility = View.VISIBLE
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