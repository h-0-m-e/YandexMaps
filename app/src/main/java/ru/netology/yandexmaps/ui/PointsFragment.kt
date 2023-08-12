package ru.netology.yandexmaps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.adapter.PointsAdapter
import ru.netology.yandexmaps.databinding.PointsFragmentBinding
import ru.netology.yandexmaps.listener.OnInteractionListener
import ru.netology.yandexmaps.viewmodel.MapViewModel

class PointsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = PointsFragmentBinding.inflate(inflater, container, false)

        val viewModel by viewModels<MapViewModel>()

        val adapter = PointsAdapter(object : OnInteractionListener {

            override fun onClick(point: ru.netology.yandexmaps.dto.Point) {
                findNavController().navigate(
                    R.id.action_pointsFragment_to_mapFragment, bundleOf(
                        MapFragment.LATITUDE_KEY to point.latitude,
                        MapFragment.LONGITUDE_KEY to point.longitude
                    )
                )
            }

            override fun onRemove(point: ru.netology.yandexmaps.dto.Point) {
                viewModel.removePointById(point.id)
            }

            override fun onEdit(point: ru.netology.yandexmaps.dto.Point) {
                PointDialog.newInstance(
                    latitude = point.latitude,
                    longitude = point.longitude,
                    id = point.id
                )
                    .show(childFragmentManager, null)
            }
        })

        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.points.collectLatest { points ->
                    adapter.submitList(points)
                    binding.empty.isVisible = points.isEmpty()
                }
            }
        }

        return binding.root
    }
}
