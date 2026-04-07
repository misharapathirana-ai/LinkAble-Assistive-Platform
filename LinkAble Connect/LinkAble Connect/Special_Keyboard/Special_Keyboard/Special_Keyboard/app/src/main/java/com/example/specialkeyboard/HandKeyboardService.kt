package com.example.specialkeyboard
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter


class HandKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var keyboard: Keyboard

    /**
     * This is called when Android wants your keyboard UI view.
     * You return the view that should appear at the bottom.
     */

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)
        keyboardView = view.findViewById(R.id.keyboardView)



        keyboardView.isPreviewEnabled = false
        keyboardView.setPreviewEnabled(false)

        keyboard = Keyboard(this, R.xml.hand_keyboard)
        keyboardView.keyboard = keyboard

        (keyboardView as SmallIconKeyboardView).prepareIcons(keyboard)

        keyboardView.setOnKeyboardActionListener(this)
        return view
    }


    /**
     * This runs when a key is pressed.
     * primaryCode is the key "code" you gave in XML android:codes=""
     */
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection ?: return

        when (primaryCode) {
            -5, Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
            10 -> ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            32 -> ic.commitText(" ", 1)

            else -> {
                // Our A-Z keys are -3001 to -3026
                if (primaryCode in -3026..-3001) {

                    // Index: A=1, B=2, ... Z=26
                    val letterIndex = (-primaryCode) - 3000

                    // If you want A to be ssl_023 (as your example), set BASE = 23
                    val BASE = 23

                    // A -> 23, B -> 24, ... Z -> 48 (change BASE if you want different)
                    val codeNumber = BASE + (letterIndex - 1)

                    val code = "ssl_" + codeNumber.toString().padStart(3, '0')
                    ic.commitText(code, 1)
                }
            }
        }
    }

    // Required methods for the listener (can keep empty)
    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
}
