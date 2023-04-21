package com.example.hello

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
fun start2ndActivity(ctx: Context) {
    ctx.startActivity(Intent(ctx, SecondActivity::class.java))
}

class SecondActivity : ComponentActivity() {

    private val mRoot: FrameLayout by lazy { findViewById(R.id.act_2nd_root) }
    private val mTvUpdate: TextView by lazy { findViewById(R.id.act_tv_update) }
    private val mIvSnapshot: ImageView by lazy { findViewById(R.id.act_2nd_iv_snapshot) }
    private lateinit var cacheView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2nd)
        window.decorView.setBackgroundColor(Color.CYAN)

        cacheView = BookAnimUtil.inst.getPageView(this)
        cacheView.visibility = View.VISIBLE
        mRoot.addView(
            cacheView, mRoot.indexOfChild(mTvUpdate), FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        mTvUpdate.setOnClickListener {
            cacheView.text = """
                    阅读详情页
                    
                    正文内容：
                    
                    ViewGroup.LayoutParams.MATCH_PARENT
                    
                    setOnClickListener
                    
                    ViewGroup.LayoutParams.MATCH_PARENT
                """.trimIndent()
        }
        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val bitmap = BookAnimUtil.createBitmap(cacheView)
                mIvSnapshot.setImageBitmap(bitmap)
                BookAnimUtil.inst.updateSnapshotBefore(bitmap)
                finish()
            }
        })
    }
}