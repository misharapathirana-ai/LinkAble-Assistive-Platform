package com.example.specialkeyboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        val btnOpenTelegram = findViewById<Button>(R.id.btnOpenTelegram)

        btnOpenTelegram.setOnClickListener {
            val telegramIntent = packageManager.getLaunchIntentForPackage("org.telegram.messenger")
            if (telegramIntent != null) {
                startActivity(telegramIntent)
            } else {
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger")
                )
                startActivity(playStoreIntent)
            }
        }
    }
}