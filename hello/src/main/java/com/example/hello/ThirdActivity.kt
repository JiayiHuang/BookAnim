package com.example.hello

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity


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

    private val mRoot: FrameLayout by lazy { findViewById(R.id.act_3rd_root) }
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(-1, -1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3rd)

        val cacheView = BookAnimUtil.inst.getPageView(this)
//        cacheView.text = "SecondActivity"
        cacheView.scaleX = 1f
        cacheView.scaleY = 1f
        cacheView.translationX = 0f
        cacheView.translationY = 0f
        mRoot.addView(
            cacheView, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}