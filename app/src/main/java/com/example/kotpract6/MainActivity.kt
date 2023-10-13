package com.example.kotpract6

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.kotpract6.databinding.ActivityMainBinding
import kotlinx.coroutines.newSingleThreadContext
import java.io.File
import java.io.IOException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val dir=File(this.dataDir,"Downloaded")
        if (!dir.exists()){
            dir.mkdir()
        }


        binding.downlBtn.setOnClickListener {
            val downlImageUrl = binding.urlEdt.text.toString()
            val destinationPath = dir.path

            lifecycleScope.launch(Dispatchers.IO){
                downloadAndSaveImage(downlImageUrl, destinationPath)
            }
        }
        setContentView(binding.root)


    }
    private suspend fun downloadAndSaveImage(imageUrl: String, destinationPath: String) {
        val network = newSingleThreadContext("Network")
        val disk = newSingleThreadContext("Disk")

        try {
            val resource = withContext(network) {
                Glide.with(this@MainActivity)
                    .asFile()
                    .load(imageUrl)
                    .submit()
                    .get()
            }

            val destinationFile = File(destinationPath)

            withContext(disk) {
                resource.copyTo(destinationFile, true)
            }

            withContext(Dispatchers.Main) {
                Glide.with(binding.imageView.context).load(destinationFile).into(binding.imageView)
            }

            // Успешно скопировали изображение
        } catch (e: IOException) {
            e.printStackTrace()
            // Обработка ошибки, если что-то пошло не так при загрузке или копировании
        }
    }
}