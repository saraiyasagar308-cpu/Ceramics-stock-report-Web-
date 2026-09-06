package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CeramicDao {
  @Query("SELECT * FROM ceramic_items ORDER BY name ASC")
  fun getAllItems(): Flow<List<CeramicItem>>

  @Query("SELECT * FROM ceramic_items WHERE id = :id")
  fun getItemById(id: Long): Flow<CeramicItem?>

  @Query("SELECT * FROM ceramic_items WHERE quantity <= minThreshold ORDER BY quantity ASC")
  fun getLowStockItems(): Flow<List<CeramicItem>>

  @Query("SELECT COUNT(*) FROM ceramic_items")
  suspend fun getItemCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertItem(item: CeramicItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertItems(items: List<CeramicItem>)

  @Update
  suspend fun updateItem(item: CeramicItem)

  @Query("UPDATE ceramic_items SET quantity = :newQuantity, lastUpdated = :updatedTime WHERE id = :id")
  suspend fun updateStock(id: Long, newQuantity: Int, updatedTime: Long)

  @Delete
  suspend fun deleteItem(item: CeramicItem)

  @Query("DELETE FROM ceramic_items WHERE id = :id")
  suspend fun deleteItemById(id: Long)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(tx: StockTransaction): Long

  @Query("SELECT * FROM stock_transactions ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentTransactions(limit: Int = 30): Flow<List<StockTransaction>>
}
