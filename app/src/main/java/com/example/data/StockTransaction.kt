package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_transactions")
data class StockTransaction(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val ceramicItemId: Long,
  val itemName: String,
  val changeAmount: Int, // Positive for in, negative for out
  val newQuantity: Int,
  val reason: String, // "Restock", "Sale", "Kiln Unload", "Breakage/Loss", "Manual Count"
  val timestamp: Long = System.currentTimeMillis()
)
