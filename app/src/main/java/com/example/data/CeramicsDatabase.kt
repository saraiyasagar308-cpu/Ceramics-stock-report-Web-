package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CeramicItem::class, StockTransaction::class], version = 1, exportSchema = false)
abstract class CeramicsDatabase : RoomDatabase() {
  abstract fun ceramicDao(): CeramicDao

  companion object {
    @Volatile
    private var INSTANCE: CeramicsDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): CeramicsDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          CeramicsDatabase::class.java,
          "ceramics_stock_db"
        )
        .addCallback(CeramicsDatabaseCallback(scope))
        .build()
        INSTANCE = instance
        instance
      }
    }
  }

  private class CeramicsDatabaseCallback(
    private val scope: CoroutineScope
  ) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
      super.onCreate(db)
      INSTANCE?.let { database ->
        scope.launch(Dispatchers.IO) {
          populateInitialStock(database.ceramicDao())
        }
      }
    }

    suspend fun populateInitialStock(dao: CeramicDao) {
      val defaultItems = listOf(
        CeramicItem(
          name = "Speckled Stoneware Mug",
          sku = "CRM-MUG-01",
          category = "Tableware",
          stage = "Finished / Glazed",
          clayBody = "Stoneware",
          glazeType = "Celadon Dip",
          quantity = 24,
          minThreshold = 8,
          unitCost = 6.50,
          unitPrice = 26.00,
          location = "Shelf A-1",
          firingTemp = "Cone 6 (1222°C)",
          notes = "12oz wheel-thrown mug with pulled handle. Best seller."
        ),
        CeramicItem(
          name = "Ribbed Pasta Bowl",
          sku = "CRM-BWL-03",
          category = "Tableware",
          stage = "Finished / Glazed",
          clayBody = "Stoneware",
          glazeType = "Matte White",
          quantity = 14,
          minThreshold = 6,
          unitCost = 9.00,
          unitPrice = 34.00,
          location = "Shelf A-2",
          firingTemp = "Cone 6 (1222°C)",
          notes = "Shallow pasta bowl with carved exterior fluting."
        ),
        CeramicItem(
          name = "Pour-Over Coffee Dripper",
          sku = "CRM-DRP-02",
          category = "Tableware",
          stage = "Finished / Glazed",
          clayBody = "Buff Clay",
          glazeType = "Ochre Satin",
          quantity = 4,
          minThreshold = 6,
          unitCost = 8.20,
          unitPrice = 32.00,
          location = "Shelf A-3",
          firingTemp = "Cone 6 (1222°C)",
          notes = "Fits standard #02 cone filters. Reorder scheduled."
        ),
        CeramicItem(
          name = "Moon Jar Porcelain Vase",
          sku = "CRM-VAS-10",
          category = "Vases & Vessels",
          stage = "Finished / Glazed",
          clayBody = "Porcelain",
          glazeType = "Clear Satin",
          quantity = 6,
          minThreshold = 3,
          unitCost = 22.00,
          unitPrice = 85.00,
          location = "Showroom Gallery",
          firingTemp = "Cone 10 (1285°C)",
          notes = "Hand-joined spheres in traditional Korean moon jar silhouette."
        ),
        CeramicItem(
          name = "Brutalist Fluted Vessel",
          sku = "CRM-VAS-14",
          category = "Vases & Vessels",
          stage = "Finished / Glazed",
          clayBody = "Terracotta",
          glazeType = "Raw Terracotta",
          quantity = 8,
          minThreshold = 4,
          unitCost = 18.50,
          unitPrice = 68.00,
          location = "Shelf B-1",
          firingTemp = "Cone 04 (1060°C)",
          notes = "Coil-built textured vessel, burnished raw surface."
        ),
        CeramicItem(
          name = "Tenmoku Ikebana Vase",
          sku = "CRM-VAS-18",
          category = "Vases & Vessels",
          stage = "Finished / Glazed",
          clayBody = "Stoneware",
          glazeType = "Tenmoku",
          quantity = 2,
          minThreshold = 4,
          unitCost = 16.00,
          unitPrice = 60.00,
          location = "Showroom Gallery",
          firingTemp = "Cone 10 (1285°C)",
          notes = "High-iron reduction glaze with rust highlights."
        ),
        CeramicItem(
          name = "Terracotta Footed Planter 6\"",
          sku = "CRM-PLT-06",
          category = "Planters & Pots",
          stage = "Finished / Glazed",
          clayBody = "Terracotta",
          glazeType = "Raw Terracotta",
          quantity = 32,
          minThreshold = 10,
          unitCost = 5.00,
          unitPrice = 22.00,
          location = "Drying Rack 3",
          firingTemp = "Cone 04 (1060°C)",
          notes = "Porous earthenware with base drainage hole."
        ),
        CeramicItem(
          name = "Hanging Ceramic Cachepot",
          sku = "CRM-PLT-08",
          category = "Planters & Pots",
          stage = "Finished / Glazed",
          clayBody = "Stoneware",
          glazeType = "Speckled Buff",
          quantity = 11,
          minThreshold = 5,
          unitCost = 7.80,
          unitPrice = 28.00,
          location = "Shelf B-2",
          firingTemp = "Cone 6 (1222°C)",
          notes = "With brass hanging loop holes."
        ),
        CeramicItem(
          name = "Sculptural Arch Bookends",
          sku = "CRM-DEC-05",
          category = "Sculptural",
          stage = "Finished / Glazed",
          clayBody = "Stoneware",
          glazeType = "Matte White",
          quantity = 0,
          minThreshold = 4,
          unitCost = 15.00,
          unitPrice = 55.00,
          location = "Shelf C-1",
          firingTemp = "Cone 6 (1222°C)",
          notes = "Sold out. Next kiln load scheduled for Friday."
        ),
        CeramicItem(
          name = "Raw B-Mix Clay (25kg Bag)",
          sku = "RAW-CLY-01",
          category = "Clay & Raw Materials",
          stage = "Raw Material",
          clayBody = "Stoneware",
          glazeType = "None",
          quantity = 16,
          minThreshold = 5,
          unitCost = 24.00,
          unitPrice = 38.00,
          location = "Clay Storage Bin",
          firingTemp = "Cone 5-6",
          notes = "Smooth stoneware throwing clay body."
        ),
        CeramicItem(
          name = "Cone 6 Celadon Glaze (5L)",
          sku = "GLZ-CEL-01",
          category = "Glazes & Underglazes",
          stage = "Raw Material",
          clayBody = "N/A",
          glazeType = "Celadon",
          quantity = 3,
          minThreshold = 5,
          unitCost = 35.00,
          unitPrice = 54.00,
          location = "Glaze Mixing Station",
          firingTemp = "Cone 6",
          notes = "Studio master mix. Low level warning."
        ),
        CeramicItem(
          name = "Porcelain Bisqueware Mugs (Unfinished)",
          sku = "BSQ-MUG-02",
          category = "Tableware",
          stage = "Bisqueware",
          clayBody = "Porcelain",
          glazeType = "Unfinished",
          quantity = 20,
          minThreshold = 8,
          unitCost = 4.20,
          unitPrice = 14.00,
          location = "Kiln Room Shelves",
          firingTemp = "Cone 06 (1000°C)",
          notes = "Awaiting glaze dipping before final glaze fire."
        )
      )
      dao.insertItems(defaultItems)

      // Seed initial transactions
      dao.insertTransaction(
        StockTransaction(
          ceramicItemId = 1,
          itemName = "Speckled Stoneware Mug",
          changeAmount = 12,
          newQuantity = 24,
          reason = "Kiln Unload Batch #42"
        )
      )
      dao.insertTransaction(
        StockTransaction(
          ceramicItemId = 9,
          itemName = "Sculptural Arch Bookends",
          changeAmount = -4,
          newQuantity = 0,
          reason = "Gallery Order #104"
        )
      )
    }
  }
}
