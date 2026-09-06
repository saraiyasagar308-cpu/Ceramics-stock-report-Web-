package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ceramic_items")
data class CeramicItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val sku: String,
  val category: String, // Tableware, Vases & Vessels, Planters & Pots, Sculptural, Clay & Raw Materials, Glazes
  val stage: String, // Finished / Glazed, Bisqueware, Greenware, Raw Material
  val clayBody: String, // Stoneware, Porcelain, Terracotta, Earthenware, Buff Clay
  val glazeType: String, // Celadon, Matte White, Tenmoku, Clear Satin, Raw Terracotta, Speckled Buff
  val quantity: Int,
  val minThreshold: Int,
  val unitCost: Double, // Production / Material cost ($)
  val unitPrice: Double, // Retail / Selling price ($)
  val location: String, // Shelf A-1, Drying Rack, Kiln Room, Showroom
  val firingTemp: String = "Cone 6 (1222°C)",
  val lastUpdated: Long = System.currentTimeMillis(),
  val notes: String = ""
) {
  val totalCostValue: Double
    get() = quantity * unitCost

  val totalRetailValue: Double
    get() = quantity * unitPrice

  val isLowStock: Boolean
    get() = quantity in 1..minThreshold

  val isOutOfStock: Boolean
    get() = quantity <= 0
}
