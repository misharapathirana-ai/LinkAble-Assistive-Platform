package com.example.specialkeyboard

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet

class SmallIconKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : KeyboardView(context, attrs) {

    private val perKeyboardCache = mutableMapOf<Int, Map<Int, Bitmap>>()
    private var iconBitmapMap: Map<Int, Bitmap> = emptyMap()

    private val tileInset = dpF(2f)
    private val tileRadius = dpF(10f)
    private val signPaddingPx = dp(8)

    private val tileFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFFFFF")
    }

    private val tileBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#D6D6D6")
        strokeWidth = dpF(0.9f)
    }

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        isDither = true
    }

    init {
        isPreviewEnabled = false
    }

    fun getBitmapForCode(code: Int): Bitmap? = iconBitmapMap[code]

    fun prepareIcons(kb: Keyboard) {
        val keyHash = System.identityHashCode(kb)

        perKeyboardCache[keyHash]?.let {
            iconBitmapMap = it
            invalidate()
            return
        }

        val map = mutableMapOf<Int, Bitmap>()

        for (key in kb.keys) {
            val code = key.codes.firstOrNull() ?: continue
            val icon: Drawable = key.icon ?: continue

            val bmp = drawableToBitmap(icon)
            val trimmed = trimTransparent(bmp, 8)
            map[code] = trimmed

            key.icon = null
            key.label = null
        }

        iconBitmapMap = map
        perKeyboardCache[keyHash] = map
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val kb = keyboard ?: return

        // draw sign tiles
        for (key in kb.keys) {
            val rect = RectF(
                key.x + tileInset,
                key.y + tileInset,
                key.x + key.width - tileInset,
                key.y + key.height - tileInset
            )
            canvas.drawRoundRect(rect, tileRadius, tileRadius, tileFillPaint)
            canvas.drawRoundRect(rect, tileRadius, tileRadius, tileBorderPaint)
        }

        // draw sign icons
        for (key in kb.keys) {
            val code = key.codes.firstOrNull() ?: continue
            val bmp = iconBitmapMap[code] ?: continue

            val availW = (key.width - 2 * signPaddingPx).coerceAtLeast(1)
            val availH = (key.height - 2 * signPaddingPx).coerceAtLeast(1)

            val scale = minOf(
                availW.toFloat() / bmp.width,
                availH.toFloat() / bmp.height
            ) * 0.88f

            val drawW = (bmp.width * scale).toInt().coerceAtLeast(1)
            val drawH = (bmp.height * scale).toInt().coerceAtLeast(1)

            val left = key.x + (key.width - drawW) / 2
            val top = key.y + (key.height - drawH) / 2

            val dst = Rect(left, top, left + drawW, top + drawH)
            canvas.drawBitmap(bmp, null, dst, bitmapPaint)
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val w = drawable.intrinsicWidth.takeIf { it > 0 } ?: 256
        val h = drawable.intrinsicHeight.takeIf { it > 0 } ?: 256

        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    private fun trimTransparent(src: Bitmap, alphaThreshold: Int): Bitmap {
        val w = src.width
        val h = src.height

        var minX = w
        var minY = h
        var maxX = -1
        var maxY = -1

        for (y in 0 until h) {
            for (x in 0 until w) {
                if (Color.alpha(src.getPixel(x, y)) > alphaThreshold) {
                    if (x < minX) minX = x
                    if (y < minY) minY = y
                    if (x > maxX) maxX = x
                    if (y > maxY) maxY = y
                }
            }
        }

        if (maxX < minX || maxY < minY) return src

        return Bitmap.createBitmap(
            src,
            minX,
            minY,
            maxX - minX + 1,
            maxY - minY + 1
        )
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun dpF(value: Float): Float = value * resources.displayMetrics.density
}