package ru.netology.yandexmaps.dto

data class Point(
    val id: Long = 0,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    )
