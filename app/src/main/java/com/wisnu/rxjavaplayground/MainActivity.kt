package com.wisnu.rxjavaplayground

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        go_to_search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        go_to_styled_text.setOnClickListener {
            val intent = Intent(this, StylingTextActivity::class.java)
            startActivity(intent)
        }

        go_to_validate_form.setOnClickListener {
            val intent = Intent(this, ValidateFormActivity::class.java)
            startActivity(intent)
        }

        go_to_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

}
