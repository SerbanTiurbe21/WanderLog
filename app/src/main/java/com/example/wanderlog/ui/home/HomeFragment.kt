package com.example.wanderlog.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wanderlog.R
import com.example.wanderlog.database.dto.UserDTO
import com.example.wanderlog.database.models.Trip
import com.example.wanderlog.databinding.FragmentHomeBinding
import com.example.wanderlog.recyclerview.TripAdapter
import com.google.gson.Gson

class HomeFragment : Fragment(), TripAdapter.TripUpdateListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var tripAdapter: TripAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonAddTrip.setOnClickListener {
            root.findNavController().navigate(R.id.action_nav_home_to_nav_addTrip)
        }

        setupRecyclerView()
        setupSwipeRefreshLayout()

        return root
    }

    private fun setupRecyclerView() {
        tripAdapter = TripAdapter(emptySet(), this)
        binding.recyclerViewTrips.apply {
            adapter = tripAdapter
            layoutManager = LinearLayoutManager(context)
        }

        homeViewModel.trips.observe(viewLifecycleOwner) { trips ->
            tripAdapter.updateTrips(trips)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        loadData()
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData() {
        val sharedPreferences = requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val user = sharedPreferences.getString("USER", null)

        if(user != null){
            val userDTO: UserDTO = Gson().fromJson(user, UserDTO::class.java)
            val userId: String = userDTO.id
            homeViewModel.fetchTrips(userId)
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTripUpdate(trip: Trip) {
        homeViewModel.updateTrip(trip)
    }
}