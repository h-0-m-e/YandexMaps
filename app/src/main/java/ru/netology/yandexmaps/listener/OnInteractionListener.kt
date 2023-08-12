package ru.netology.yandexmaps.listener

import ru.netology.yandexmaps.dto.Point

interface OnInteractionListener {
    fun onClick(point: Point)
    fun onRemove(point: Point)
    fun onEdit(point: Point)
}
