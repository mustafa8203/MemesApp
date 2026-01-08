package com.example.meme

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class EditActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var topEditText: EditText
    private lateinit var bottomEditText: EditText

    private var originalBitmap: Bitmap? = null

    private var topTextSize = 100f
    private var bottomTextSize = 100f

    companion object {
        private const val PERMISSION_REQUEST_WRITE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit)

        // ربط العناصر
        imageView = findViewById(R.id.imageView)
        topEditText = findViewById(R.id.topText)
        bottomEditText = findViewById(R.id.bottomText)

        // باقي الأزرار
        val btnApply = findViewById<Button>(R.id.btnApply)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnShare = findViewById<Button>(R.id.btnShare)

        // استقبال الصورة من الجهاز
        val imageUri = intent.getParcelableExtra<Uri>("imageUri")
        val templateResId = intent.getIntExtra("templateResId", -1)

        when {
            imageUri != null -> {
                imageView.setImageURI(imageUri)
                imageView.post {
                    originalBitmap = imageView.drawable.toBitmap().copy(Bitmap.Config.ARGB_8888, true)
                }
            }
            templateResId != -1 -> {
                imageView.setImageResource(templateResId)
                imageView.post {
                    originalBitmap = imageView.drawable.toBitmap().copy(Bitmap.Config.ARGB_8888, true)
                }
            }
            else -> {
                Toast.makeText(this, "لم يتم تمرير صورة أو قالب!", Toast.LENGTH_SHORT).show()
            }
        }

        //     تطبيق النص، تكبير وتصغير، حفظ ومشاركة
        btnApply.setOnClickListener { applyTextToImage() }

        // تكبير وتصغير النصوص
        val btnIncreaseTop = findViewById<Button>(R.id.btnIncreaseTop)
        val btnDecreaseTop = findViewById<Button>(R.id.btnDecreaseTop)
        val btnIncreaseBottom = findViewById<Button>(R.id.btnIncreaseBottom)
        val btnDecreaseBottom = findViewById<Button>(R.id.btnDecreaseBottom)

        btnIncreaseTop.setOnClickListener { topTextSize += 20f; applyTextToImage() }
        btnDecreaseTop.setOnClickListener { if (topTextSize > 20f) topTextSize -= 20f; applyTextToImage() }
        btnIncreaseBottom.setOnClickListener { bottomTextSize += 20f; applyTextToImage() }
        btnDecreaseBottom.setOnClickListener { if (bottomTextSize > 20f) bottomTextSize -= 20f; applyTextToImage() }

        // حفظ ومشاركة
        btnSave.setOnClickListener { if (checkPermission()) saveImageWithText() else requestPermission() }
        btnShare.setOnClickListener { shareImageWithText() }
    }


    // =============================
    // صلاحيات
    // =============================
    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) true
        else ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_WRITE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_WRITE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                saveImageWithText()
            } else {
                Toast.makeText(this, "تم رفض الإذن", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // =============================
    // تطبيق النص على الصورة
    // =============================
    private fun applyTextToImage() {
        val base = originalBitmap ?: return
        val bmp = base.copy(Bitmap.Config.ARGB_8888, true)
        applyTextToBitmap(bmp)
        imageView.setImageBitmap(bmp)
    }

    private fun applyTextToBitmap(bitmap: Bitmap) {
        val canvas = Canvas(bitmap)
        drawWrappedText(canvas, topEditText.text.toString(), topTextSize, true)
        drawWrappedText(canvas, bottomEditText.text.toString(), bottomTextSize, false)
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        textSize: Float,
        isTop: Boolean
    ) {
        if (text.isBlank()) return

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            this.textSize = textSize
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            setShadowLayer(6f, 0f, 0f, Color.BLACK)
        }

        val maxWidth = canvas.width * 0.9f
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val test = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(test) <= maxWidth) {
                currentLine = test
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)

        val lineHeight = textSize + 12f
        val startY =
            if (isTop) 60f + lineHeight
            else canvas.height - (lines.size * lineHeight) - 40f

        for (i in lines.indices) {
            canvas.drawText(
                lines[i],
                canvas.width / 2f,
                startY + (i * lineHeight),
                paint
            )
        }
    }

    // =============================
    // حفظ الصورة في المعرض
    // =============================
    private fun saveImageWithText() {
        originalBitmap?.let {
            val bmp = it.copy(Bitmap.Config.ARGB_8888, true)
            applyTextToBitmap(bmp)
            saveImageToGallery(bmp)
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val filename = "Meme_${System.currentTimeMillis()}.png"
        try {
            val fos: OutputStream =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/MemeApp")
                    }
                    val uri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )!!
                    contentResolver.openOutputStream(uri)!!
                } else {
                    val dir = File(
                        android.os.Environment.getExternalStoragePublicDirectory(
                            android.os.Environment.DIRECTORY_DCIM
                        ), "MemeApp"
                    )
                    if (!dir.exists()) dir.mkdirs()
                    FileOutputStream(File(dir, filename))
                }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            Toast.makeText(this, "تم الحفظ في المعرض ✅", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "فشل الحفظ ❌", Toast.LENGTH_SHORT).show()
        }
    }

    // =============================
    // مشاركة الصورة
    // =============================
    private fun shareImageWithText() {
        originalBitmap?.let {
            val bmp = it.copy(Bitmap.Config.ARGB_8888, true)
            applyTextToBitmap(bmp)

            val cacheDir = File(cacheDir, "images")
            cacheDir.mkdirs()

            val file = File(cacheDir, "share.png")
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()

            val uri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, "مشاركة الصورة"))
        }
    }
}
