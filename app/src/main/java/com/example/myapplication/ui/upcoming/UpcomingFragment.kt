package com.example.myapplication.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ui.common.EventsAdapter
import com.example.myapplication.data.response.ListEventsItem
import com.example.myapplication.databinding.FragmentUpcomingBinding
import com.example.myapplication.viewmodel.MainViewModel
import com.example.myapplication.ui.detail.DetailEventActivity
import com.google.android.material.snackbar.Snackbar

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView dengan LinearLayoutManager
        val adapter = EventsAdapter<ListEventsItem>(emptyList()) { event ->
            openEventDetail(event)
        }
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter

        // Memanggil fungsi untuk mendapatkan acara yang akan datang
        eventViewModel.getUpcomingEvents()

        // Mengamati upcomingEvents untuk acara yang akan datang
        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            adapter.updateEvents(events)
        }

        // Mengamati perubahan isLoading
        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Menampilkan pesan Snackbar jika ada
        eventViewModel.snackbarText.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun openEventDetail(event: ListEventsItem) {
        val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
            putExtra("eventId", event.id.toString())
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
