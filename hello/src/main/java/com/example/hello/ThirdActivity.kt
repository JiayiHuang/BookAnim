package com.example.hello

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback


/**
 * <pre>
 *     @author Jiun
 *     date   :2023/04/20/15:15
 *     desc   : description
 *     version:
 * </pre>
 */
fun start3rdActivity(ctx: Context) {
    ctx.startActivity(Intent(ctx, ThirdActivity::class.java))
}

class ThirdActivity : ComponentActivity() {

    private val durationAnim = 1000L
    private val mRoot: FrameLayout by lazy { findViewById(R.id.act_3rd_root) }
    private val mIvCover: ImageView by lazy { findViewById(R.id.act_3rd_iv_cover) }
    private val mTv: TextView by lazy { findViewById(R.id.act_3rd_tv) }
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(-1, -1)
        theme.applyStyle(R.style.activity_transparent, true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3rd)

        mTv.visibility = GONE

        val cacheView = Anim2ndUtil.inst.getPageView(this)
        cacheView.apply {
            scaleX = 1f
            scaleY = 1f
            translationX = 0f
            translationY = 0f
            visibility = INVISIBLE
            mRoot.addView(
                this, 0, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }
        // 获取封面 bitmap
        mIvCover.apply {
            visibility = GONE
            setImageBitmap(BookAnimUtil.inst.genCoverBitmap())
            val info = Anim2ndUtil.inst.startViewInfo
            layoutParams.apply {
                height = info.height.toInt()
                width = info.width.toInt()
                (this as MarginLayoutParams).apply {
                    topMargin = info.top.toInt()
                    leftMargin = info.left.toInt()
                }
            }
        }
        Anim2ndUtil.inst.setAnimStartViews(mRoot, mIvCover, cacheView)

        cacheView.post {
            AnimatorSet().apply {
                duration = durationAnim
                playTogether(Anim2ndUtil.inst.coverAnim(true).apply {
                    addAll(Anim2ndUtil.inst.bgAnim(true))
                })
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        cacheView.visibility = VISIBLE
                        mIvCover.visibility = VISIBLE
                    }
                })
            }.start()
        }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AnimatorSet().apply {
                    duration = durationAnim
                    playTogether(Anim2ndUtil.inst.coverAnim(false).apply {
                        addAll(Anim2ndUtil.inst.bgAnim(false))
                    })
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            cacheView.visibility = VISIBLE
                            mIvCover.visibility = VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            finish()
                            overridePendingTransition(0, 0)
                        }
                    })
                }.start()
            }
        })
    }
}