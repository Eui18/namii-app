package com.namii.app.features.map.presentation.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

object MapPinHelper {

    fun createCategoryPinDrawable(
        context: Context,
        @ColorInt categoryColor: Int,
        isSelected: Boolean
    ): Drawable {
        val sizeDp = if (isSelected) 54 else 38
        val sizePx = (sizeDp * context.resources.displayMetrics.density).toInt().coerceAtLeast(32)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val cx = sizePx / 2f
        val cy = sizePx * 0.42f
        val radius = sizePx * 0.27f

        if (isSelected) {
            val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = ColorUtils.setAlphaComponent(categoryColor, 68)
            }
            canvas.drawCircle(cx, sizePx * 0.45f, sizePx * 0.45f, haloPaint)
        }

        val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = categoryColor
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = sizePx * 0.08f
        }
        val innerDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }

        val tipPath = Path().apply {
            moveTo(cx - radius * 0.72f, cy + radius * 0.52f)
            lineTo(cx, sizePx * 0.90f)
            lineTo(cx + radius * 0.72f, cy + radius * 0.52f)
            close()
        }

        canvas.drawPath(tipPath, pinPaint)
        canvas.drawCircle(cx, cy, radius, pinPaint)
        canvas.drawCircle(cx, cy, radius, borderPaint)
        canvas.drawCircle(cx, cy, radius * 0.34f, innerDotPaint)

        return BitmapDrawable(context.resources, bitmap)
    }

    fun createSellerPickerDrawable(
        context: Context,
        @ColorInt accentColor: Int
    ): Drawable {
        val sizePx = (52 * context.resources.displayMetrics.density).toInt().coerceAtLeast(44)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val cx = sizePx / 2f
        val cy = sizePx * 0.38f
        val radius = sizePx * 0.28f

        // Halo suave para destacar que el pin es arrastrable
        canvas.drawCircle(
            cx,
            sizePx * 0.46f,
            sizePx * 0.42f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = ColorUtils.setAlphaComponent(accentColor, 54)
            }
        )

        val tipPath = Path().apply {
            moveTo(cx - radius * 0.74f, cy + radius * 0.52f)
            lineTo(cx, sizePx * 0.94f)
            lineTo(cx + radius * 0.74f, cy + radius * 0.52f)
            close()
        }

        canvas.drawPath(tipPath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentColor })
        canvas.drawCircle(cx, cy, radius, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentColor })
        canvas.drawCircle(
            cx,
            cy,
            radius,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                style = Paint.Style.STROKE
                strokeWidth = sizePx * 0.08f
            }
        )
        canvas.drawCircle(cx, cy, radius * 0.36f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })

        return BitmapDrawable(context.resources, bitmap)
    }

    fun createUserLocationDrawable(context: Context): Drawable {
        val sizePx = (28 * context.resources.displayMetrics.density).toInt().coerceAtLeast(24)
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val center = sizePx / 2f
        val primaryBlue = Color.parseColor("#3E7CB1")

        canvas.drawCircle(
            center,
            center,
            sizePx * 0.46f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ColorUtils.setAlphaComponent(primaryBlue, 56) }
        )
        canvas.drawCircle(
            center,
            center,
            sizePx * 0.28f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        )
        canvas.drawCircle(
            center,
            center,
            sizePx * 0.21f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = primaryBlue }
        )

        return BitmapDrawable(context.resources, bitmap)
    }
}
