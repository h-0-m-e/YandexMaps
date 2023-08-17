package ru.netology.yandexmaps.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.databinding.FragmentAddPointBinding
import ru.netology.yandexmaps.dto.Point
import ru.netology.yandexmaps.types.PointType
import ru.netology.yandexmaps.viewmodel.MapViewModel

class AddPointFragment : Fragment() {

    companion object {
        const val ID_KEY = "ID_KEY"
        const val TITLE_KEY = "TITLE_KEY"
        const val DESCRIPTION_KEY = "DESCRIPTION_KEY"
        const val LATITUDE_KEY = "LATITUDE_KEY"
        const val LONGITUDE_KEY = "LONGITUDE_KEY"

    }

    private val viewModel by viewModels<MapViewModel>()

    private val types = listOf(
        R.drawable.location_48,
        R.drawable.atm_48,
        R.drawable.bus_48,
        R.drawable.car_48,
        R.drawable.dining_48,
        R.drawable.home_48,
        R.drawable.sleep_48,
        R.drawable.store_48,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddPointBinding.inflate(inflater, container, false)

        binding.apply {

            titleField.text = Editable.Factory.getInstance().newEditable(
                requireArguments().getString(TITLE_KEY) ?: ""
            )
            descriptionField.text = Editable.Factory.getInstance().newEditable(
                requireArguments().getString(DESCRIPTION_KEY) ?: ""
            )

            var counter = 0
            selector.setOnClickListener {
                if (counter == types.size - 1) {
                    counter = 0
                    selector.setImageResource(types[counter])
                } else {
                    counter++
                    selector.setImageResource(types[counter])
                }
            }


            save.setOnClickListener {


                val title = titleField.text.toString().trim()
                val description = descriptionField.text.toString().trim()

                if (title.isBlank()) {
                    emptyTitleMsg.isVisible = true
                } else {
                    val currentType = types[counter]
                    viewModel.insertPoint(
                        Point(
                            id = requireArguments().getLong(ID_KEY),
                            title = title,
                            description = description,
                            latitude = requireArguments().getDouble(LATITUDE_KEY),
                            longitude = requireArguments().getDouble(LONGITUDE_KEY),
                            pointType = when (currentType) {
                                R.drawable.location_48 -> PointType.DEFAULT
                                R.drawable.atm_48 -> PointType.ATM
                                R.drawable.bus_48 -> PointType.BUS
                                R.drawable.car_48 -> PointType.CAR
                                R.drawable.dining_48 -> PointType.DINING
                                R.drawable.home_48 -> PointType.HOME
                                R.drawable.sleep_48 -> PointType.SLEEP
                                R.drawable.store_48 -> PointType.STORE
                                else -> PointType.DEFAULT
                            }
                        )
                    )
                    findNavController().popBackStack()
                }
            }

        }

        return binding.root
    }
}
