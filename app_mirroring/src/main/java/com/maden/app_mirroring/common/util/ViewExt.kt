package com.maden.app_mirroring.common.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View

fun View.toBitmap(): Bitmap? {
    return try {
        val screenshotBitmap = Bitmap.createBitmap(
            this.width,
            this.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(screenshotBitmap)
        canvas.drawColor(Color.WHITE)
        this.draw(canvas)

        Bitmap.createBitmap(screenshotBitmap)
    } catch (e: Exception) {
        null
    }
}