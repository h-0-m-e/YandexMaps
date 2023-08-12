package ru.netology.yandexmaps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.db.PointDb
import ru.netology.yandexmaps.dto.Point
import ru.netology.yandexmaps.entity.PointEntity

class MapViewModel(context: Application): AndroidViewModel(context) {

    private val data = PointDb.getInstance(context).pointDao()
    val points = data.getAll().map {
        it.map(PointEntity::toDto)
    }

    fun insertPoint(point: Point) {
        viewModelScope.launch {
            data.insert(PointEntity.fromDto(point))
        }
    }

    fun removePointById(id: Long) {
        viewModelScope.launch {
            data.removeById(id)
        }
    }

}
