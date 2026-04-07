package com.example.specialkeyboard

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet

class SmallIconKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : KeyboardView(context, attrs) {

    private val paddingPx = dp(6)
    private val iconMap = mutableMapOf<Int, Drawable>()

    // Special keys
    private val CODE_BACKSPACE = Keyboard.KEYCODE_DELETE   // -5
    private val CODE_SPACE = 32
    private val CODE_ENTER = 10
    private val CODE_DONE = -1000

    private val specialCodes = setOf(CODE_BACKSPACE, CODE_SPACE, CODE_ENTER, CODE_DONE)

    // Matching rounded design for special keys
    private val radius = dpF(18f)
    private val inset = dpF(2f)

    private val specialFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFFFFF") // soft card look
    }

    private val specialBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#D6D6D6")
        strokeWidth = dpF(1.5f)
    }

    fun prepareIcons(kb: Keyboard) {
        iconMap.clear()

        for (k in kb.keys) {
            val code = k.codes.firstOrNull() ?: continue
            val icon = k.icon ?: continue

            val unique = icon.constantState?.newDrawable()?.mutate() ?: icon.mutate()
            val bmp = drawableToBitmap(unique)
            val trimmed = trimTransparent(bmp, alphaThreshold = 10)

            iconMap[code] = BitmapDrawable(resources, trimmed)

            // stop default icon drawing
            k.icon = null
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val kb = keyboard ?: return

        // 1) Draw special key "cards" behind everything
        for (key in kb.keys) {
            val code = key.codes.firstOrNull() ?: continue
            if (code in specialCodes) {
                val rect = RectF(
                    key.x + inset,
                    key.y + inset,
                    key.x + key.width - inset,
                    key.y + key.height - inset
                )
                canvas.drawRoundRect(rect, radius, radius, specialFillPaint)
                canvas.drawRoundRect(rect, radius, radius, specialBorderPaint)
            }
        }

        // 2) Draw default labels (⌫, ↵, ✓ etc.)
        super.onDraw(canvas)

        // 3) Draw sign icons (A–Z) scaled + centered
        for (key in kb.keys) {
            val code = key.codes.firstOrNull() ?: continue
            val icon = iconMap[code] ?: continue

            val save = canvas.save()
            canvas.clipRect(key.x, key.y, key.x + key.width, key.y + key.height)

            val availW = (key.width - 2 * paddingPx).coerceAtLeast(1)
            val availH = (key.height - 2 * paddingPx).coerceAtLeast(1)

            val iw = icon.intrinsicWidth.coerceAtLeast(1)
            val ih = icon.intrinsicHeight.coerceAtLeast(1)

            // Fill more so it matches special keys visually
            val scale = minOf(availW.toFloat() / iw, availH.toFloat() / ih) * 0.90f
            val drawW = (iw * scale).toInt()
            val drawH = (ih * scale).toInt()

            val left = key.x + (key.width - drawW) / 2
            val top = key.y + (key.height - drawH) / 2

            icon.bounds = Rect(left, top, left + drawW, top + drawH)
            icon.draw(canvas)

            canvas.restoreToCount(save)
        }
    }

    private fun drawableToBitmap(d: Drawable): Bitmap {
        val w = d.intrinsicWidth.takeIf { it > 0 } ?: 256
        val h = d.intrinsicHeight.takeIf { it > 0 } ?: 256
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        d.setBounds(0, 0, c.width, c.height)
        d.draw(c)
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
        return Bitmap.createBitmap(src, minX, minY, (maxX - minX + 1), (maxY - minY + 1))
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
    private fun dpF(v: Float): Float = v * resources.displayMetrics.density
}
