package com.example.myapplication.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ui.common.EventsAdapter
import com.example.myapplication.database.Favorite
import com.example.myapplication.databinding.FragmentFavoriteBinding
import com.example.myapplication.ui.detail.DetailEventActivity
import com.example.myapplication.viewmodel.FavoriteViewModel
import com.example.myapplication.factory.FavoriteViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteViewModelFactory.getInstance(requireActivity().application)
    }

    private lateinit var adapter: EventsAdapter<Favorite>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView dengan LinearLayoutManager
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        adapter = EventsAdapter(emptyList()) { favorite ->
            openEventDetail(favorite)
        }
        binding.rvEvents.adapter = adapter

        // Mengamati perubahan pada daftar favorit
        favoriteViewModel.getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            adapter.updateEvents(favorites)
            binding.tvEmpty.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
        }

        // Mengamati perubahan pada isLoading untuk menampilkan ProgressBar
        favoriteViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Mengamati perubahan pada snackbarText untuk menampilkan Snackbar
        favoriteViewModel.snackbarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun openEventDetail(favorite: Favorite) {
        val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
            putExtra("eventId", favorite.id.toString())
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
