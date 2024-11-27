package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ui.common.EventsAdapter
import com.example.myapplication.data.response.ListEventsItem
import com.example.myapplication.viewmodel.MainViewModel
import com.example.myapplication.ui.detail.DetailEventActivity
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var upcomingEventsAdapter: EventsAdapter<ListEventsItem>
    private lateinit var finishedEventsAdapter: EventsAdapter<ListEventsItem>
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeEvents()
    }

    private fun setupRecyclerViews() {
        upcomingEventsAdapter = EventsAdapter(emptyList()) { event ->
            openEventDetail(event)
        }
        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvUpcomingEvents.adapter = upcomingEventsAdapter

        finishedEventsAdapter = EventsAdapter(emptyList()) { event ->
            openEventDetail(event)
        }
        binding.rvFinishedEvents.layoutManager = LinearLayoutManager(context)
        binding.rvFinishedEvents.adapter = finishedEventsAdapter
    }

    private fun observeEvents() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            val upcomingEvents = events.take(5)
            upcomingEventsAdapter.updateEvents(upcomingEvents)
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            val finishedEvents = events.take(5)
            finishedEventsAdapter.updateEvents(finishedEvents)
        }

        viewModel.getUpcomingEvents()
        viewModel.getFinishedEvents()
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
