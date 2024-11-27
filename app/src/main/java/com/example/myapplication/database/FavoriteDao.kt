package com.example.myapplication.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    // Memasukkan event ke dalam daftar favorit
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favorite: Favorite)

    // Menghapus event dari daftar favorit
    @Delete
    fun delete(favorite: Favorite)

    // Mendapatkan semua data favorit yang disimpan
    @Query("SELECT * FROM favorite ORDER BY id ASC")
    fun getAllFavorites(): LiveData<List<Favorite>>

    // Mengecek apakah suatu event sudah difavoritkan berdasarkan ID
    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE id = :id)")
    fun isFavorite(id: Int): LiveData<Boolean> // Pastikan menggunakan id yang benar
}
