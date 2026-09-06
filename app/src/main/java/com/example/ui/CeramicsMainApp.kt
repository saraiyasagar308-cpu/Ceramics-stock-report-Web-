package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.TerracottaPrimary

@Composable
fun CeramicsMainApp(
  viewModel: CeramicsViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val items by viewModel.filteredItems.collectAsStateWithLifecycle()
  val allItems by viewModel.allItems.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val selectedStatus by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()
  val metrics by viewModel.reportMetrics.collectAsStateWithLifecycle()
  val transactions by viewModel.recentTransactions.collectAsStateWithLifecycle()

  val isAddEditVisible by viewModel.isAddEditSheetVisible.collectAsStateWithLifecycle()
  val editingItem by viewModel.editingItem.collectAsStateWithLifecycle()
  val adjustingItem by viewModel.adjustingItem.collectAsStateWithLifecycle()
  val detailItem by viewModel.viewingDetailItem.collectAsStateWithLifecycle()

  Scaffold(
    topBar = {
      CeramicsTopBar(
        onShareReport = { viewModel.shareWebReport(context) },
        onOpenAdd = { viewModel.openAddDialog() }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.navigationBars)
          .testTag("ceramics_bottom_nav")
      ) {
        NavigationBarItem(
          selected = currentTab == CeramicsTab.INVENTORY,
          onClick = { viewModel.selectTab(CeramicsTab.INVENTORY) },
          icon = {
            Icon(
              imageVector = if (currentTab == CeramicsTab.INVENTORY) Icons.Filled.Category else Icons.Outlined.Category,
              contentDescription = "Inventory"
            )
          },
          label = { Text("Inventory", fontWeight = if (currentTab == CeramicsTab.INVENTORY) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = TerracottaPrimary,
            selectedTextColor = TerracottaPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_inventory")
        )

        NavigationBarItem(
          selected = currentTab == CeramicsTab.REPORT_DASHBOARD,
          onClick = { viewModel.selectTab(CeramicsTab.REPORT_DASHBOARD) },
          icon = {
            Icon(
              imageVector = if (currentTab == CeramicsTab.REPORT_DASHBOARD) Icons.Filled.Assessment else Icons.Outlined.Assessment,
              contentDescription = "Stock Report"
            )
          },
          label = { Text("Stock Report", fontWeight = if (currentTab == CeramicsTab.REPORT_DASHBOARD) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = TerracottaPrimary,
            selectedTextColor = TerracottaPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_report")
        )

        NavigationBarItem(
          selected = currentTab == CeramicsTab.WEB_REPORT_SITE,
          onClick = { viewModel.selectTab(CeramicsTab.WEB_REPORT_SITE) },
          icon = {
            Icon(
              imageVector = if (currentTab == CeramicsTab.WEB_REPORT_SITE) Icons.Filled.Language else Icons.Outlined.Language,
              contentDescription = "Web Site View"
            )
          },
          label = { Text("Web Report", fontWeight = if (currentTab == CeramicsTab.WEB_REPORT_SITE) FontWeight.Bold else FontWeight.Normal) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = TerracottaPrimary,
            selectedTextColor = TerracottaPrimary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_web_view")
        )
      }
    },
    floatingActionButton = {
      if (currentTab == CeramicsTab.INVENTORY) {
        FloatingActionButton(
          onClick = { viewModel.openAddDialog() },
          containerColor = TerracottaPrimary,
          contentColor = Color.White,
          modifier = Modifier.testTag("fab_add_ceramic_item")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Item")
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (currentTab) {
        CeramicsTab.INVENTORY -> {
          CeramicsInventoryScreen(
            items = items,
            searchQuery = searchQuery,
            onSearchChange = { viewModel.setSearch(it) },
            selectedCategory = selectedCategory,
            onCategorySelect = { viewModel.setCategory(it) },
            selectedStatus = selectedStatus,
            onStatusSelect = { viewModel.setStatusFilter(it) },
            onItemClick = { viewModel.openItemDetail(it) },
            onQuickAdjustStock = { viewModel.openStockAdjustment(it) },
            onAddItem = { viewModel.openAddDialog() },
            metrics = metrics
          )
        }

        CeramicsTab.REPORT_DASHBOARD -> {
          CeramicsReportScreen(
            metrics = metrics,
            items = allItems,
            transactions = transactions,
            onSwitchToWebView = { viewModel.selectTab(CeramicsTab.WEB_REPORT_SITE) },
            onShareReport = { viewModel.shareWebReport(context) },
            onCopyHtml = { viewModel.copyWebReportHtml(context) },
            onItemClick = { viewModel.openItemDetail(it) }
          )
        }

        CeramicsTab.WEB_REPORT_SITE -> {
          CeramicsWebReportView(
            items = allItems,
            onShareReport = { viewModel.shareWebReport(context) },
            onCopyHtml = { viewModel.copyWebReportHtml(context) }
          )
        }
      }
    }

    // Modal Dialogs
    if (isAddEditVisible) {
      AddEditCeramicDialog(
        item = editingItem,
        onDismiss = { viewModel.closeAddEditDialog() },
        onSave = { viewModel.saveItem(it) }
      )
    }

    adjustingItem?.let { itemToAdjust ->
      StockAdjustmentDialog(
        item = itemToAdjust,
        onDismiss = { viewModel.closeStockAdjustment() },
        onConfirm = { delta, reason ->
          viewModel.adjustStock(itemToAdjust, delta, reason)
        }
      )
    }

    detailItem?.let { currentDetailItem ->
      ItemDetailSheet(
        item = currentDetailItem,
        onDismiss = { viewModel.closeItemDetail() },
        onEdit = {
          viewModel.closeItemDetail()
          viewModel.openEditDialog(currentDetailItem)
        },
        onAdjustStock = {
          viewModel.closeItemDetail()
          viewModel.openStockAdjustment(currentDetailItem)
        },
        onDelete = {
          viewModel.deleteItem(currentDetailItem)
        }
      )
    }
  }
}
