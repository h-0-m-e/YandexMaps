package ru.netology.yandexmaps.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.dto.Point
import ru.netology.yandexmaps.viewmodel.MapViewModel

class PointDialog: DialogFragment() {

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val LATITUDE_KEY = "LATITUDE_KEY"
        private const val LONGITUDE_KEY = "LONGITUDE_KEY"
        fun newInstance(latitude: Double, longitude: Double, id: Long? = null) = PointDialog().apply {
            arguments = bundleOf(LATITUDE_KEY to latitude, LONGITUDE_KEY to longitude, ID_KEY to id)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel by viewModels<MapViewModel>()
        val view = AppCompatEditText(requireContext())
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(getString(R.string.enter_title))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val text = view.text?.toString()?.takeIf { it.isNotBlank() } ?: run {
                    Toast.makeText(requireContext(), "Title is empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.insertPoint(
                    Point(
                        id = requireArguments().getLong(ID_KEY) as? Long ?: 0,
                        title = text,
                        description = "",
                        latitude = requireArguments().getDouble(LATITUDE_KEY),
                        longitude = requireArguments().getDouble(LONGITUDE_KEY),
                    )
                )
            }
            .create()
    }

}
