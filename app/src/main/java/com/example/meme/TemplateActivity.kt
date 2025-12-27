package com.example.meme

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TemplateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template)

        val template1 = findViewById<ImageView>(R.id.template1)
        val template2 = findViewById<ImageView>(R.id.template2)
        val template3 = findViewById<ImageView>(R.id.template3)
        val template4 = findViewById<ImageView>(R.id.template4)
        val template5 = findViewById<ImageView>(R.id.template5)
        val template6 = findViewById<ImageView>(R.id.template6)
        val template7 = findViewById<ImageView>(R.id.template7)
        val template8 = findViewById<ImageView>(R.id.template8)
        val template9 = findViewById<ImageView>(R.id.template9)


        template1.setOnClickListener {
            openEditActivity(R.drawable.tepm1)
        }

        template2.setOnClickListener {
            openEditActivity(R.drawable.tepm2)
        }


        template3.setOnClickListener {
            openEditActivity(R.drawable.tepm3)
        }
        template4.setOnClickListener {
            openEditActivity(R.drawable.tepm4)
        }
        template5.setOnClickListener {
            openEditActivity(R.drawable.tepm5)
        }
        template6.setOnClickListener {
            openEditActivity(R.drawable.tepm6)
        }
        template7.setOnClickListener {
            openEditActivity(R.drawable.tepm7)
        }
        template8.setOnClickListener {
            openEditActivity(R.drawable.tepm8)
        }
        template9.setOnClickListener {
            openEditActivity(R.drawable.tepm9)
        }

    }

    private fun openEditActivity(templateResId: Int) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra("templateResId", templateResId)
        startActivity(intent)
    }
}
