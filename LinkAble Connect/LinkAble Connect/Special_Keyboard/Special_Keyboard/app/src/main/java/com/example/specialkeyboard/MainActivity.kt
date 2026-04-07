package com.example.specialkeyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnImeSettings = findViewById<Button>(R.id.btnImeSettings)
        val btnGuidePage = findViewById<Button>(R.id.btnGuidePage)
        val btnAboutPage = findViewById<Button>(R.id.btnAboutPage)
        val faqListView = findViewById<ExpandableListView>(R.id.faqListView)

        btnImeSettings.setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        btnGuidePage.setOnClickListener {
            startActivity(Intent(this, GuideActivity::class.java))
        }

        btnAboutPage.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        setupFaqList(faqListView)
    }

    private fun setupFaqList(listView: ExpandableListView) {
        val questions = listOf(
            "Why was this app built?",
            "Does this app type by itself?",
            "Why do I need to enable the keyboard manually?",
            "Where can I use this keyboard?"
        )

        val answers = listOf(
            "This app was built to support a special hand-signal based keyboard system for users who need a more structured and custom input method.",
            "No. The app itself does not type. The keyboard service handles text insertion when the user presses a key.",
            "Android requires custom keyboards to be enabled from the device settings for security and user control.",
            "You can use it in supported text fields such as chat apps, notes, browsers, and other input areas."
        )

        val groupData = ArrayList<Map<String, String>>()
        val childData = ArrayList<List<Map<String, String>>>()

        for (i in questions.indices) {
            val groupMap = HashMap<String, String>()
            groupMap["QUESTION"] = questions[i]
            groupData.add(groupMap)

            val children = ArrayList<Map<String, String>>()
            val childMap = HashMap<String, String>()
            childMap["ANSWER"] = answers[i]
            children.add(childMap)

            childData.add(children)
        }

        val adapter = SimpleExpandableListAdapter(
            this,
            groupData,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf("QUESTION"),
            intArrayOf(android.R.id.text1),
            childData,
            android.R.layout.simple_list_item_1,
            arrayOf("ANSWER"),
            intArrayOf(android.R.id.text1)
        )

        listView.setAdapter(adapter)
    }
}