package com.example.data

import kotlinx.coroutines.flow.Flow

class CeramicsRepository(private val dao: CeramicDao) {
  val allItems: Flow<List<CeramicItem>> = dao.getAllItems()
  val lowStockItems: Flow<List<CeramicItem>> = dao.getLowStockItems()
  val recentTransactions: Flow<List<StockTransaction>> = dao.getRecentTransactions()

  fun getItemById(id: Long): Flow<CeramicItem?> = dao.getItemById(id)

  suspend fun insertItem(item: CeramicItem): Long = dao.insertItem(item)

  suspend fun updateItem(item: CeramicItem) = dao.updateItem(item)

  suspend fun deleteItem(item: CeramicItem) = dao.deleteItem(item)

  suspend fun deleteItemById(id: Long) = dao.deleteItemById(id)

  suspend fun adjustStock(item: CeramicItem, delta: Int, reason: String) {
    val newQuantity = (item.quantity + delta).coerceAtLeast(0)
    dao.updateStock(item.id, newQuantity, System.currentTimeMillis())
    dao.insertTransaction(
      StockTransaction(
        ceramicItemId = item.id,
        itemName = item.name,
        changeAmount = delta,
        newQuantity = newQuantity,
        reason = reason
      )
    )
  }

  suspend fun getItemCount(): Int = dao.getItemCount()
}
