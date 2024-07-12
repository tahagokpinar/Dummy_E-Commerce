package com.example.afinal.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.afinal.entities.Favorite

@Dao
interface FavoriteDao {

    @Insert
    fun insertFavorite(favorite: Favorite)

    @Delete
    fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite WHERE p_id = :productId AND u_id = :userId")
    fun getFavoriteById(productId: Long, userId: Long): Favorite?

    @Query("SELECT * FROM favorite WHERE u_id = :userId")
    fun getAllFavorites(userId: Long): List<Favorite>
}