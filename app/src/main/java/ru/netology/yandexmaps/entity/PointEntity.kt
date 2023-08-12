package ru.netology.yandexmaps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.yandexmaps.dto.Point

@Entity
data class PointEntity constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
) {
    companion object {
        fun fromDto(dto: Point): PointEntity = with(dto) {
            PointEntity(
                id = id,
                title = title,
                description = description,
                latitude = latitude,
                longitude = longitude
            )
        }
    }

    fun toDto(): Point = Point(
        id = id,
        title = title,
        description = description,
        latitude = latitude,
        longitude = longitude
    )
}
