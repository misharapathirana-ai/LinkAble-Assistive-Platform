package com.example.specialkeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

class HandKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView

    private lateinit var previewBar: FrameLayout
    private lateinit var previewIcon: ImageView

    private lateinit var btnLanguage: TextView
    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var btnSpace: TextView
    private lateinit var btnEnter: TextView
    private lateinit var btnDone: TextView
    private lateinit var btnBackspace: TextView

    private val uiHandler = Handler(Looper.getMainLooper())
    private var hideRunnable: Runnable? = null

    private enum class Panel { ENGLISH, SINHALA }

    private var panel: Panel = Panel.ENGLISH
    private var siPage = 0

    private val siPages = listOf(
        R.xml.sinhala_keyboard_p1,
        R.xml.sinhala_keyboard_p2,
        R.xml.sinhala_keyboard_p3,
        R.xml.sinhala_keyboard_p4
    )

    private val keyboardCache = HashMap<Int, Keyboard>()
    private var lastXmlLoaded: Int? = null

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)

        keyboardView = view.findViewById(R.id.keyboardView)
        previewBar = view.findViewById(R.id.signPreviewBar)
        previewIcon = view.findViewById(R.id.signPreviewIcon)

        btnLanguage = view.findViewById(R.id.btnLanguage)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        btnSpace = view.findViewById(R.id.btnSpace)
        btnEnter = view.findViewById(R.id.btnEnter)
        btnDone = view.findViewById(R.id.btnDone)
        btnBackspace = view.findViewById(R.id.btnBackspace)

        keyboardView.isPreviewEnabled = false
        keyboardView.setOnKeyboardActionListener(this)

        wireBottomBar()
        loadKeyboard(force = true)
        updateBottomBar()

        return view
    }

    private fun wireBottomBar() {
        btnLanguage.setOnClickListener {
            if (panel == Panel.ENGLISH) {
                panel = Panel.SINHALA
                siPage = 0
            } else {
                panel = Panel.ENGLISH
            }
            loadKeyboard(force = true)
            updateBottomBar()
        }

        btnPrev.setOnClickListener {
            if (panel == Panel.SINHALA) {
                siPage = if (siPage - 1 < 0) siPages.lastIndex else siPage - 1
                loadKeyboard(force = true)
                updateBottomBar()
            }
        }

        btnNext.setOnClickListener {
            if (panel == Panel.SINHALA) {
                siPage = (siPage + 1) % siPages.size
                loadKeyboard(force = true)
                updateBottomBar()
            }
        }

        btnSpace.setOnClickListener {
            currentInputConnection?.commitText(" ", 1)
        }

        btnEnter.setOnClickListener {
            val ic = currentInputConnection ?: return@setOnClickListener
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }

        btnDone.setOnClickListener {
            requestHideSelf(0)
        }

        btnBackspace.setOnClickListener {
            currentInputConnection?.deleteSurroundingText(1, 0)
        }

        btnBackspace.setOnLongClickListener {
            currentInputConnection?.deleteSurroundingText(1, 0)
            true
        }
    }

    private fun updateBottomBar() {
        btnLanguage.text = if (panel == Panel.ENGLISH) "සි" else "EN"
        btnPrev.alpha = if (panel == Panel.SINHALA) 1f else 0.45f
        btnNext.alpha = if (panel == Panel.SINHALA) 1f else 0.45f
    }

    private fun currentXml(): Int {
        return if (panel == Panel.ENGLISH) {
            R.xml.hand_keyboard
        } else {
            siPages[siPage]
        }
    }

    private fun loadKeyboard(force: Boolean = false) {
        val xml = currentXml()
        if (!force && lastXmlLoaded == xml) return

        lastXmlLoaded = xml
        val kb = keyboardCache.getOrPut(xml) { Keyboard(this, xml) }
        keyboardView.keyboard = kb

        (keyboardView as? SmallIconKeyboardView)?.prepareIcons(kb)

        keyboardView.invalidateAllKeys()
        keyboardView.requestLayout()
    }

    private fun showSignPreview(primaryCode: Int) {
        val bmp = (keyboardView as? SmallIconKeyboardView)?.getBitmapForCode(primaryCode) ?: return

        previewIcon.setImageBitmap(bmp)
        previewBar.visibility = View.VISIBLE

        previewBar.startAnimation(AlphaAnimation(0f, 1f).apply { duration = 90 })

        hideRunnable?.let { uiHandler.removeCallbacks(it) }

        hideRunnable = Runnable {
            previewBar.startAnimation(AlphaAnimation(1f, 0f).apply { duration = 140 })
            previewBar.visibility = View.GONE
        }

        uiHandler.postDelayed(hideRunnable!!, 650)
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection ?: return

        when {
            primaryCode in -3026..-3001 -> {
                showSignPreview(primaryCode)
                val idx = (-primaryCode) - 3000
                val letter = ('a'.code + idx - 1).toChar()
                ic.commitText("ssl_$letter", 1)
            }

            primaryCode in -5062..-5001 -> {
                showSignPreview(primaryCode)
                val idx = (-primaryCode) - 5000
                ic.commitText("slsl_" + idx.toString().padStart(2, '0'), 1)
            }
        }
    }

    override fun swipeUp() {}
    override fun swipeDown() {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {}
}