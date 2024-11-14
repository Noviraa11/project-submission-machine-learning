package com.dicoding.asclepius.view

import android.content.Intent
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedImageUri: Uri? = null
    private lateinit var classifier: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classifier = ImageClassifierHelper(this)

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            selectedImageUri?.let { uri ->
                analyzeImage(uri)
            } ?: showToast("Please select an image first")
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun analyzeImage(uri: Uri) {
        val bitmap = loadBitmapFromUri(uri) ?: run {
            showToast("Failed to load image")
            return
        }

        val (resultText, confidence) = classifier.classifyImage(bitmap)
        moveToResult(resultText, confidence, uri)
    }

    private fun moveToResult(resultText: String, confidence: Float, uri: Uri) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("RESULT_TEXT", resultText)
            putExtra("CONFIDENCE", confidence)
            putExtra("IMAGE_URI", uri.toString())
        }
        startActivity(intent)
    }

    @Suppress("DEPRECATION")
    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
