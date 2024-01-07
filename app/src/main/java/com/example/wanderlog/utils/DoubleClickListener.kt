package com.example.wanderlog.utils

import android.view.View

class DoubleClickListener(
    private val doubleClickTimeDelta: Long = 500,
    private val onDoubleClick: (View) -> Unit
) : View.OnClickListener {

    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < doubleClickTimeDelta) {
            onDoubleClick(v)
        }
        lastClickTime = clickTime
    }
}
