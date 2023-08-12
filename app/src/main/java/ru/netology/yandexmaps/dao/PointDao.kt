package ru.netology.yandexmaps.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.yandexmaps.entity.PointEntity

@Dao
interface PointDao {

    @Query("SELECT * FROM PointEntity")
    fun getAll(): Flow<List<PointEntity>>

    @Query("DELETE FROM PointEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: PointEntity)
}
