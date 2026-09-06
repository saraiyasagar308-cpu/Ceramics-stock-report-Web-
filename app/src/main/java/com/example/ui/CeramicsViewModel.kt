package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CeramicItem
import com.example.data.CeramicsDatabase
import com.example.data.CeramicsRepository
import com.example.data.StockTransaction
import com.example.report.WebReportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class CeramicsTab(val title: String) {
  INVENTORY("Inventory"),
  REPORT_DASHBOARD("Stock Report"),
  WEB_REPORT_SITE("Web Report View")
}

data class StockReportMetrics(
  val totalItemsCount: Int = 0,
  val totalSkus: Int = 0,
  val totalCostValue: Double = 0.0,
  val totalRetailValue: Double = 0.0,
  val totalGrossMargin: Double = 0.0,
  val marginPercentage: Double = 0.0,
  val lowStockCount: Int = 0,
  val outOfStockCount: Int = 0,
  val categoryBreakdown: Map<String, CategoryMetric> = emptyMap(),
  val stageBreakdown: Map<String, Int> = emptyMap()
)

data class CategoryMetric(
  val skuCount: Int,
  val totalUnits: Int,
  val costValue: Double,
  val retailValue: Double,
  val sharePercentage: Double
)

class CeramicsViewModel(
  application: Application,
  private val repository: CeramicsRepository
) : AndroidViewModel(application) {

  val currentTab = MutableStateFlow(CeramicsTab.INVENTORY)
  val searchQuery = MutableStateFlow("")
  val selectedCategory = MutableStateFlow("All")
  val selectedStatusFilter = MutableStateFlow("All") // All, Low Stock, Out of Stock, In Stock

  // Active item for dialogs
  val editingItem = MutableStateFlow<CeramicItem?>(null)
  val isAddEditSheetVisible = MutableStateFlow(false)
  val adjustingItem = MutableStateFlow<CeramicItem?>(null)
  val viewingDetailItem = MutableStateFlow<CeramicItem?>(null)

  val allItems: StateFlow<List<CeramicItem>> = repository.allItems
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val recentTransactions: StateFlow<List<StockTransaction>> = repository.recentTransactions
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Filtered items list
  val filteredItems: StateFlow<List<CeramicItem>> = combine(
    allItems,
    searchQuery,
    selectedCategory,
    selectedStatusFilter
  ) { items, query, category, status ->
    items.filter { item ->
      val matchesQuery = query.isBlank() ||
          item.name.contains(query, ignoreCase = true) ||
          item.sku.contains(query, ignoreCase = true) ||
          item.clayBody.contains(query, ignoreCase = true) ||
          item.glazeType.contains(query, ignoreCase = true) ||
          item.location.contains(query, ignoreCase = true)

      val matchesCategory = category == "All" || item.category.equals(category, ignoreCase = true)

      val matchesStatus = when (status) {
        "Low Stock" -> item.isLowStock
        "Out of Stock" -> item.isOutOfStock
        "In Stock" -> !item.isOutOfStock && !item.isLowStock
        else -> true
      }

      matchesQuery && matchesCategory && matchesStatus
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Report Metrics
  val reportMetrics: StateFlow<StockReportMetrics> = allItems.combine(allItems) { items, _ ->
    val totalCount = items.sumOf { it.quantity }
    val totalSkus = items.size
    val costVal = items.sumOf { it.totalCostValue }
    val retailVal = items.sumOf { it.totalRetailValue }
    val margin = (retailVal - costVal).coerceAtLeast(0.0)
    val marginPct = if (retailVal > 0) (margin / retailVal * 100) else 0.0
    val lowStock = items.count { it.isLowStock }
    val outOfStock = items.count { it.isOutOfStock }

    val catBreakdown = items.groupBy { it.category }.mapValues { (_, catItems) ->
      val units = catItems.sumOf { it.quantity }
      val cVal = catItems.sumOf { it.totalCostValue }
      val rVal = catItems.sumOf { it.totalRetailValue }
      val share = if (totalCount > 0) (units.toDouble() / totalCount * 100) else 0.0
      CategoryMetric(
        skuCount = catItems.size,
        totalUnits = units,
        costValue = cVal,
        retailValue = rVal,
        sharePercentage = share
      )
    }

    val stageMap = items.groupBy { it.stage }.mapValues { (_, stageItems) ->
      stageItems.sumOf { it.quantity }
    }

    StockReportMetrics(
      totalItemsCount = totalCount,
      totalSkus = totalSkus,
      totalCostValue = costVal,
      totalRetailValue = retailVal,
      totalGrossMargin = margin,
      marginPercentage = marginPct,
      lowStockCount = lowStock,
      outOfStockCount = outOfStock,
      categoryBreakdown = catBreakdown,
      stageBreakdown = stageMap
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = StockReportMetrics()
  )

  fun selectTab(tab: CeramicsTab) {
    currentTab.value = tab
  }

  fun setSearch(query: String) {
    searchQuery.value = query
  }

  fun setCategory(category: String) {
    selectedCategory.value = category
  }

  fun setStatusFilter(status: String) {
    selectedStatusFilter.value = status
  }

  fun openAddDialog() {
    editingItem.value = null
    isAddEditSheetVisible.value = true
  }

  fun openEditDialog(item: CeramicItem) {
    editingItem.value = item
    isAddEditSheetVisible.value = true
  }

  fun closeAddEditDialog() {
    isAddEditSheetVisible.value = false
    editingItem.value = null
  }

  fun openStockAdjustment(item: CeramicItem) {
    adjustingItem.value = item
  }

  fun closeStockAdjustment() {
    adjustingItem.value = null
  }

  fun openItemDetail(item: CeramicItem) {
    viewingDetailItem.value = item
  }

  fun closeItemDetail() {
    viewingDetailItem.value = null
  }

  fun saveItem(item: CeramicItem) {
    viewModelScope.launch {
      if (item.id == 0L) {
        val newId = repository.insertItem(item)
        repository.adjustStock(item.copy(id = newId), 0, "Initial Entry")
      } else {
        repository.updateItem(item)
      }
      closeAddEditDialog()
    }
  }

  fun deleteItem(item: CeramicItem) {
    viewModelScope.launch {
      repository.deleteItem(item)
      if (viewingDetailItem.value?.id == item.id) {
        viewingDetailItem.value = null
      }
    }
  }

  fun adjustStock(item: CeramicItem, delta: Int, reason: String) {
    viewModelScope.launch {
      repository.adjustStock(item, delta, reason)
      closeStockAdjustment()
    }
  }

  fun shareWebReport(context: Context) {
    val items = allItems.value
    val html = WebReportGenerator.generateHtmlReport(items)
    WebReportGenerator.shareWebReport(context, html, items)
  }

  fun copyWebReportHtml(context: Context) {
    val items = allItems.value
    val html = WebReportGenerator.generateHtmlReport(items)
    WebReportGenerator.copyHtmlToClipboard(context, html)
  }

  fun getGeneratedHtml(): String {
    return WebReportGenerator.generateHtmlReport(allItems.value)
  }

  companion object {
    fun provideFactory(application: Application): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val database = CeramicsDatabase.getDatabase(application, (application as? CoroutineScopeOwner)?.scope ?: kotlinx.coroutines.GlobalScope)
          val repository = CeramicsRepository(database.ceramicDao())
          return CeramicsViewModel(application, repository) as T
        }
      }
  }
}

interface CoroutineScopeOwner {
  val scope: kotlinx.coroutines.CoroutineScope
}
