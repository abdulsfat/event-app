package com.example.myapplication.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.database.Favorite
import com.example.myapplication.databinding.ActivityDetailEventBinding
import com.example.myapplication.factory.FavoriteViewModelFactory
import com.example.myapplication.viewmodel.EventViewModel
import com.example.myapplication.viewmodel.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar

class DetailEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailEventBinding
    private lateinit var currentEvent: Favorite
    private var isFavorited = false
    private val eventViewModel: EventViewModel by viewModels()
    private lateinit var favoriteViewModel: FavoriteViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoriteViewModel = ViewModelProvider(
            this,
            FavoriteViewModelFactory.getInstance(application)
        )[FavoriteViewModel::class.java]

        val eventId = intent.getStringExtra("eventId") ?: ""

        if (eventId.isNotEmpty()) {
            eventViewModel.getEventDetail(eventId)
        }

        // update UI
        eventViewModel.eventDetail.observe(this) { eventDetail ->
            eventDetail?.let { event ->
                currentEvent = Favorite(
                    id = event.id,
                    name = event.name,
                    mediaCover = event.mediaCover
                )


                binding.tvEventTitle.text = event.name
                binding.tvOwnerName.text = event.ownerName
                binding.tvEventDescription.text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_LEGACY)
                binding.tvEventTime.text = "${event.beginTime} - ${event.endTime}"
                binding.tvCityName.text = event.cityName
                binding.tvEventQuota.text = "Kuota: ${event.quota - event.registrants}"

                // ngeload event image
                Glide.with(this)
                    .load(event.imageLogo)
                    .into(binding.ivEventImage)

                // Handle daftar btn
                binding.btnRegister.setOnClickListener {
                    openRegistrationPage(event.link)
                }

                // Observe is favorited
                favoriteViewModel.isFavorite(event.id).observe(this) { favorited ->
                    isFavorited = favorited
                    updateFavoriteButton()
                }

                // Handle favorite btn
                binding.fabFavorite.setOnClickListener {
                    toggleFavorite()
                }
            }
        }


        eventViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


        eventViewModel.snackbarText.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun openRegistrationPage(url: String?) {
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(intent)
        } ?: run {
            Toast.makeText(this, "Registration link not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleFavorite() {
        if (isFavorited) {
            favoriteViewModel.deleteFavorite(currentEvent)
            isFavorited = false
        } else {
            favoriteViewModel.insertFavorite(currentEvent)
            isFavorited = true
        }
        updateFavoriteButton()
    }

    private fun updateFavoriteButton() {
        val favoriteIcon = if (isFavorited) {
            R.drawable.ic_favorite
        } else {
            R.drawable.ic_favorite_border
        }
        binding.fabFavorite.setImageResource(favoriteIcon)
    }
}
