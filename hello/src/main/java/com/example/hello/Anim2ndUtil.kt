package com.example.hello

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.MutableContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

/**
 * <pre>
 *     @author Jiun
 *     date   :2023/04/24/20:01
 *     desc   : description
 *     version:
 * </pre>
 */
class Anim2ndUtil {
    companion object {
        private const val TAG = "ViewPool"

        val inst: Anim2ndUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Anim2ndUtil()
        }

    }

    /** 书籍打开页动画的根布局 */
    private lateinit var animRoot: FrameLayout

    /** 书籍封面 */
    private lateinit var coverView: View

    /** 书籍内容 */
    private var pageView: TextView? = null

    /** 书籍打开时，当前 View 的信息，用来计算动画 */
    lateinit var startViewInfo: Quadruple<Float, Float, Float, Float>

    fun clear() {
        // TODO: 资源释放
    }

    fun setAnimStartViews(
        animRoot: FrameLayout,
        coverView: View,
        bgView: TextView,
    ) {
        this.animRoot = animRoot
        this.coverView = coverView
        this.pageView = bgView
    }

    fun setAnchorInfo(startViewInfo: Quadruple<Float, Float, Float, Float>) {
        this.startViewInfo = startViewInfo
    }

    private fun createPageView(context: Activity): TextView {
        val ctx = MutableContextWrapper(context)
        pageView = TextView(ctx).apply {
            setBackgroundResource(R.drawable.shape_border)
            gravity = Gravity.CENTER
            text = "addOnAttachStateChangeListener - " + "addOnAttachStateChangeListener"
        }
        return pageView as TextView
    }

    fun getPageView(ctx: Activity): TextView {
        if (pageView == null) createPageView(ctx)
        pageView?.apply {
            (context as MutableContextWrapper).baseContext = ctx
            if (parent is ViewGroup) {
                (parent as ViewGroup).removeView(pageView)
            }
        }
        return pageView!!
    }

    private var coverScaleY = 0f

    fun coverAnim(isOpen: Boolean): MutableList<Animator> {
        coverView.pivotX = 0f
        coverView.pivotY = startViewInfo.height / 2.0f
        coverScaleY = animRoot.height * 1f / startViewInfo.height
        return mutableListOf(
            ObjectAnimator.ofFloat(
                coverView,
                "translationX",
                if (isOpen) 0f else startViewInfo.left * -1f,
                if (isOpen) startViewInfo.left * -1f else 0f
            ), ObjectAnimator.ofFloat(
                coverView,
                "translationY",
                if (isOpen) 0f else ((animRoot.height - startViewInfo.height) / 2f - startViewInfo.top),
                if (isOpen) ((animRoot.height - startViewInfo.height) / 2f - startViewInfo.top) else 0f
            ), ObjectAnimator.ofFloat(
                coverView,
                "scaleY",
                if (isOpen) 1f else coverScaleY,
                if (isOpen) coverScaleY else 1f
            ), ObjectAnimator.ofFloat(
                coverView, "rotationY", if (isOpen) 0f else -90f, if (isOpen) -90f else 0f
            )
        )
    }

    private var bgScaleX = 0f
    private var bgScaleY = 0f

    fun bgAnim(isOpen: Boolean): MutableList<Animator> {
        val pageView: TextView = this.pageView!!
        pageView.pivotX = pageView.width / 2f
        pageView.pivotY = pageView.height / 2f

        bgScaleY = coverScaleY
        bgScaleX = animRoot.width * 1f / startViewInfo.width

        val bgScaleY = ObjectAnimator.ofFloat(
            pageView, "scaleY", if (isOpen) 1f / bgScaleY else 1f, if (isOpen) 1f else 1f / bgScaleY
        ).apply {
            if (isOpen) { // 模拟图书正文的动态加载过程
                val text = pageView.text.toString()
                addUpdateListener {
                    val fraction = it.animatedFraction.toString()
                    pageView.text =
                        "$text \n\n Fraction: ${if (fraction.length > 10) fraction.substring(10) else fraction}"
                }
            }
        }

        return mutableListOf(
            bgScaleY, ObjectAnimator.ofFloat(
                pageView,
                "scaleX",
                if (isOpen) 1f / bgScaleX else 1f,
                if (isOpen) 1f else 1f / bgScaleX
            ), ObjectAnimator.ofFloat(
                pageView,
                "translationX",
                if (isOpen) -1 * ((animRoot.width - startViewInfo.width) / 2f - startViewInfo.left) else 0f,
                if (isOpen) 0f else -1 * ((animRoot.width - startViewInfo.width) / 2f - startViewInfo.left)
            ), ObjectAnimator.ofFloat(
                pageView,
                "translationY",
                if (isOpen) -1 * ((animRoot.height - startViewInfo.height) / 2f - startViewInfo.top) else 0f,
                if (isOpen) 0f else -1 * ((animRoot.height - startViewInfo.height) / 2f - startViewInfo.top),
            )
        )
    }

    fun createBitmap(view: View): Bitmap {
        val width = animRoot.measuredWidth
        val height = animRoot.measuredHeight
        val bp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bp)
        view.draw(canvas)
        canvas.save()
        return bp
    }
}