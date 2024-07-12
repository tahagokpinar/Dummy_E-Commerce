package com.example.afinal.configs

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.afinal.dao.FavoriteDao
import com.example.afinal.entities.Favorite
import com.example.afinal.utils.Converters

@Database(entities = [Favorite::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}