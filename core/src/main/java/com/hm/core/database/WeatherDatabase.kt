package com.hm.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hm.core.database.dao.WeatherDao
import com.hm.core.database.entity.WeatherEntity

@Database(
    entities = [WeatherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}

