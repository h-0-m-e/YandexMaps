package ru.netology.yandexmaps.dto

import ru.netology.yandexmaps.types.PointType

data class Point(
    val id: Long = 0,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val pointType: PointType
    )
