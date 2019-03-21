package com.example.ricardo.kotlinapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DisplayMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)

        val intent : Intent = this.intent
        val message : String = intent.getStringExtra(MainActivity.EXTRA_MESSAGE)

        val textView : TextView =  findViewById(R.id.textView)
        textView.text = message
    }
}
