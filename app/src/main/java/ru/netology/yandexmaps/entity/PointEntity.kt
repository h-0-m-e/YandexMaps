package ru.netology.yandexmaps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.yandexmaps.dto.Point
import ru.netology.yandexmaps.types.PointType

@Entity
data class PointEntity constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val pointType: PointType
) {
    companion object {
        fun fromDto(dto: Point): PointEntity = with(dto) {
            PointEntity(
                id = id,
                title = title,
                description = description,
                latitude = latitude,
                longitude = longitude,
                pointType = pointType
            )
        }
    }

    fun toDto(): Point = Point(
        id = id,
        title = title,
        description = description,
        latitude = latitude,
        longitude = longitude,
        pointType = pointType
    )

}
