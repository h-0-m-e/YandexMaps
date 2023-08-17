package ru.netology.yandexmaps.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.databinding.FragmentMapBinding
import ru.netology.yandexmaps.databinding.PointBinding
import ru.netology.yandexmaps.types.PointType
import ru.netology.yandexmaps.viewmodel.MapViewModel

class MapFragment : Fragment() {

    private var mapView: MapView? = null

    private lateinit var userLocation: UserLocationLayer
    private val listener = object : InputListener {
        override fun onMapTap(map: Map, point: com.yandex.mapkit.geometry.Point) = Unit

        override fun onMapLongTap(map: Map, point: com.yandex.mapkit.geometry.Point) {
            findNavController().navigate(
                R.id.action_mapFragment_to_addPointFragment, bundleOf(
                    LATITUDE_KEY to point.latitude,
                    LONGITUDE_KEY to point.longitude
                )
            )
        }
    }

    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(view: UserLocationView) = Unit

        override fun onObjectRemoved(view: UserLocationView) = Unit

        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocation.cameraPosition()?.target?.let {
                mapView?.map?.move(CameraPosition(it, 10F, 0F, 0F))
            }
            userLocation.setObjectListener(null)
        }
    }

    companion object {
        const val LATITUDE_KEY = "LATITUDE_KEY"
        const val LONGITUDE_KEY = "LONGITUDE_KEY"
    }


    private val viewModel by viewModels<MapViewModel>()

    private val pointTapListener = MapObjectTapListener { mapObject, _ ->
        viewModel.removePointById(mapObject.userData as Long)
        true
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    MapKitFactory.getInstance().resetLocationManagerToDefault()
                    userLocation.cameraPosition()?.target?.also {
                        val map = mapView?.map ?: return@registerForActivityResult
                        val cameraPosition = map.cameraPosition
                        map.move(
                            CameraPosition(
                                it,
                                cameraPosition.zoom,
                                cameraPosition.azimuth,
                                cameraPosition.tilt,
                            )
                        )
                    }
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.location_permission_required),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapBinding.inflate(inflater, container, false)

        mapView = binding.map.apply {
            userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            userLocation.isVisible = true
            userLocation.isHeadingEnabled = false

            map.addInputListener(listener)

            val collection = map.mapObjects.addCollection()
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.points.collectLatest { points ->
                        collection.clear()
                        points.forEach { point ->
                            val pointBinding = PointBinding.inflate(layoutInflater)
                            pointBinding.title.text = point.title
                            pointBinding.type.setImageResource(
                                when (point.pointType) {
                                    PointType.DEFAULT -> R.drawable.location_24
                                    PointType.ATM -> R.drawable.atm_24
                                    PointType.BUS -> R.drawable.bus_24
                                    PointType.CAR -> R.drawable.car_24
                                    PointType.DINING -> R.drawable.dining_24
                                    PointType.HOME -> R.drawable.home_24
                                    PointType.SLEEP -> R.drawable.sleep_24
                                    PointType.STORE -> R.drawable.store_24
                                }
                            )
                            collection.addPlacemark(
                                com.yandex.mapkit.geometry.Point(point.latitude, point.longitude),
                                ViewProvider(pointBinding.root)
                            ).apply {
                                userData = point.id
                            }
                        }
                    }
                }
            }
            collection.addTapListener(pointTapListener)

            val arguments = arguments
            if (arguments != null &&
                arguments.containsKey(LATITUDE_KEY) &&
                arguments.containsKey(LONGITUDE_KEY)
            ) {
                val cameraPosition = map.cameraPosition
                map.move(
                    CameraPosition(
                        com.yandex.mapkit.geometry.Point(
                            arguments.getDouble(LATITUDE_KEY),
                            arguments.getDouble(LONGITUDE_KEY)
                        ),
                        10F,
                        cameraPosition.azimuth,
                        cameraPosition.tilt
                    )
                )
                arguments.remove(LATITUDE_KEY)
                arguments.remove(LONGITUDE_KEY)
            } else {
                userLocation.setObjectListener(locationObjectListener)
            }
        }

        binding.zoomIn.setOnClickListener {
            binding.map.map.move(
                CameraPosition(
                    binding.map.map.cameraPosition.target,
                    binding.map.map.cameraPosition.zoom + 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null
            )
        }

        binding.zoomOut.setOnClickListener {
            binding.map.map.move(
                CameraPosition(
                    binding.map.map.cameraPosition.target,
                    binding.map.map.cameraPosition.zoom - 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null,
            )
        }

        binding.location.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.pointsList.setOnClickListener {
            findNavController().navigate(
                R.id.action_mapFragment_to_pointsFragment
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }
}
