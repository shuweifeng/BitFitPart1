package com.example.bitfitpart1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item_table")
    fun getAll(): Flow<List<ItemEntity>>
    // fun getAll(): List<ItemEntity>

    @Insert
    fun insertAll(items: List<ItemEntity>)

    @Insert
    fun insert(item: ItemEntity)

    @Query("DELETE FROM item_table")
    fun deleteAll()
}