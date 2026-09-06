package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CeramicItem
import com.example.ui.theme.AlertLowStock
import com.example.ui.theme.AlertOutOfStock
import com.example.ui.theme.CeladonSecondary
import com.example.ui.theme.StatusInStock
import com.example.ui.theme.TerracottaPrimary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CeramicsInventoryScreen(
  items: List<CeramicItem>,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  selectedCategory: String,
  onCategorySelect: (String) -> Unit,
  selectedStatus: String,
  onStatusSelect: (String) -> Unit,
  onItemClick: (CeramicItem) -> Unit,
  onQuickAdjustStock: (CeramicItem) -> Unit,
  onAddItem: () -> Unit,
  metrics: StockReportMetrics,
  modifier: Modifier = Modifier
) {
  val categories = listOf(
    "All",
    "Tableware",
    "Vases & Vessels",
    "Planters & Pots",
    "Sculptural",
    "Clay & Raw Materials",
    "Glazes & Underglazes"
  )

  val statusFilters = listOf("All", "Low Stock", "Out of Stock", "In Stock")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("ceramics_inventory_list"),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Studio Hero Banner
    item {
      CeramicsStudioHeroBanner(
        metrics = metrics,
        onOpenReport = {}
      )
    }

    // Search and Filters Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          placeholder = { Text("Search pottery, glaze, clay, SKU, location...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchChange("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear search")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_search_stock")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { category ->
            val isSelected = selectedCategory.equals(category, ignoreCase = true)
            FilterChip(
              selected = isSelected,
              onClick = { onCategorySelect(category) },
              label = { Text(category) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TerracottaPrimary,
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.testTag("filter_chip_${category.replace(" ", "_")}")
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Status Filter Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          statusFilters.forEach { status ->
            val isSelected = selectedStatus == status
            FilterChip(
              selected = isSelected,
              onClick = { onStatusSelect(status) },
              label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  if (status == "Low Stock") {
                    Icon(
                      imageVector = Icons.Default.Warning,
                      contentDescription = null,
                      modifier = Modifier.size(14.dp),
                      tint = if (isSelected) Color.White else AlertLowStock
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                  }
                  Text(status)
                }
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = when (status) {
                  "Low Stock" -> AlertLowStock
                  "Out of Stock" -> AlertOutOfStock
                  "In Stock" -> CeladonSecondary
                  else -> MaterialTheme.colorScheme.secondary
                },
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.testTag("status_chip_${status.replace(" ", "_")}")
            )
          }
        }
      }
    }

    // Results Header Count
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "INVENTORY ITEMS (${items.size})",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
          )
        )
      }
    }

    // Empty State
    if (items.isEmpty()) {
      item {
        Card(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("card_empty_inventory")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Text("🏺", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No ceramic items found",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Try adjusting your search query or category filters, or tap + to register new ware.",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    } else {
      // List of Items
      items(items, key = { it.id }) { item ->
        CeramicStockItemCard(
          item = item,
          onClick = { onItemClick(item) },
          onAdjustClick = { onQuickAdjustStock(item) },
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
      }
    }
  }
}

@Composable
fun CeramicStockItemCard(
  item: CeramicItem,
  onClick: () -> Unit,
  onAdjustClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currency = NumberFormat.getCurrencyInstance(Locale.US)

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = when {
        item.isOutOfStock -> AlertOutOfStock.copy(alpha = 0.3f)
        item.isLowStock -> AlertLowStock.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("ceramic_card_${item.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top Row: Title + Status Pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.name,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold
            ),
            maxLines = 1
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Text(
                text = item.sku,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "•",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
            Text(
              text = item.category,
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        // Status Badge
        StockStatusBadge(item = item)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Ceramic Specs Chips: Clay Body, Glaze, Stage
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = TerracottaPrimary.copy(alpha = 0.1f)
        ) {
          Text(
            text = "Clay: ${item.clayBody}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = TerracottaPrimary,
              fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = CeladonSecondary.copy(alpha = 0.12f)
        ) {
          Text(
            text = "Glaze: ${item.glazeType}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = CeladonSecondary,
              fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = item.stage,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom Row: Location, Valuation, Quantity Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Location & Price
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Place,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = item.location,
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${currency.format(item.unitPrice)} retail  (${currency.format(item.totalRetailValue)} total)",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
          )
        }

        // Quantity & Quick Adjust Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${item.quantity} pcs",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = when {
                  item.isOutOfStock -> AlertOutOfStock
                  item.isLowStock -> AlertLowStock
                  else -> MaterialTheme.colorScheme.onSurface
                }
              )
            )
            Text(
              text = "Min: ${item.minThreshold}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            )
          }

          // Stock In/Out Adjustment Button
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
              .size(38.dp)
              .clickable(onClick = onAdjustClick)
              .testTag("btn_adjust_stock_${item.id}")
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "±",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun StockStatusBadge(item: CeramicItem) {
  val (bgColor, textColor, label, icon) = when {
    item.isOutOfStock -> Quadruple(
      Color(0xFFFEE2E2),
      AlertOutOfStock,
      "OUT OF STOCK",
      Icons.Default.Clear
    )
    item.isLowStock -> Quadruple(
      Color(0xFFFEF3C7),
      AlertLowStock,
      "LOW STOCK",
      Icons.Default.Warning
    )
    else -> Quadruple(
      Color(0xFFDCFCE7),
      StatusInStock,
      "IN STOCK",
      null
    )
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor,
    modifier = Modifier.padding(start = 4.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = textColor,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
      }
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = textColor,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          letterSpacing = 0.4.sp
        )
      )
    }
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
