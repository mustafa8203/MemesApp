package com.example.meme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: ConstraintLayout

    // Launcher لاختيار صورة من الجهاز
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra("imageUri", uri)
                startActivity(intent)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main)

        val btnChooseImage = findViewById<Button>(R.id.btnChooseImage)
        val btnChooseTemplate = findViewById<Button>(R.id.btnChooseTemplate)

        // عند الضغط على اختيار صورة من الجهاز
        btnChooseImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // عند الضغط على اختيار قالب جاهز
        btnChooseTemplate.setOnClickListener {
            val intent = Intent(this, TemplateActivity::class.java)
            startActivity(intent)
        }

    }
}
