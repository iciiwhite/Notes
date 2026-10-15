package io.iciitestapps.note

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = TextView(this)
        view.text = "Hello from Notes"
        view.textSize = 20f
        view.gravity = Gravity.CENTER
        setContentView(view)
    }
}
