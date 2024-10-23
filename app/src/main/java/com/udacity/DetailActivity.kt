package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.Constants.EXTRA_REPOSITORY
import com.udacity.Constants.EXTRA_STATUS
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.okButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val repository = intent.getStringExtra(EXTRA_REPOSITORY)
        val status = intent.getStringExtra(EXTRA_STATUS)

        binding.repository.text = repository
        binding.status.text = status
    }
}
