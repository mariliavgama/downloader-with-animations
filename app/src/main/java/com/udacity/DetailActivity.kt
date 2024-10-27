package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        val status = intent.getIntExtra(EXTRA_STATUS, 0)

        binding.repository.text = repository
        binding.statusText.text = getString(status)
        if (status == R.string.status_success) {
            binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            binding.statusImage.setImageResource(R.drawable.ic_success)
        } else {
            binding.statusText.setTextColor(ContextCompat.getColor(this, R.color.design_default_color_error))
            binding.statusImage.setImageResource(R.drawable.ic_fail)
        }

    }
}
