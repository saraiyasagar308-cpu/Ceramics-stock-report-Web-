package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditCeramicDialog(
  item: CeramicItem?,
  onDismiss: () -> Unit,
  onSave: (CeramicItem) -> Unit
) {
  var name by remember { mutableStateOf(item?.name ?: "") }
  var sku by remember { mutableStateOf(item?.sku ?: "CRM-${Random.nextInt(100, 999)}") }
  var category by remember { mutableStateOf(item?.category ?: "Tableware") }
  var stage by remember { mutableStateOf(item?.stage ?: "Finished / Glazed") }
  var clayBody by remember { mutableStateOf(item?.clayBody ?: "Stoneware") }
  var glazeType by remember { mutableStateOf(item?.glazeType ?: "Celadon Dip") }
  var firingTemp by remember { mutableStateOf(item?.firingTemp ?: "Cone 6 (1222°C)") }
  var quantityText by remember { mutableStateOf((item?.quantity ?: 10).toString()) }
  var minThresholdText by remember { mutableStateOf((item?.minThreshold ?: 5).toString()) }
  var unitCostText by remember { mutableStateOf((item?.unitCost ?: 7.50).toString()) }
  var unitPriceText by remember { mutableStateOf((item?.unitPrice ?: 28.00).toString()) }
  var location by remember { mutableStateOf(item?.location ?: "Shelf A-1") }
  var notes by remember { mutableStateOf(item?.notes ?: "") }

  var errorMessage by remember { mutableStateOf("") }

  val categories = listOf("Tableware", "Vases & Vessels", "Planters & Pots", "Sculptural", "Clay & Raw Materials", "Glazes & Underglazes")
  val stages = listOf("Finished / Glazed", "Bisqueware", "Greenware", "Raw Material")
  val clayBodies = listOf("Stoneware", "Porcelain", "Terracotta", "Buff Clay", "Earthenware")
  val glazeTypes = listOf("Celadon Dip", "Matte White", "Tenmoku", "Raw Terracotta", "Clear Satin", "Speckled Buff", "None")
  val firingCones = listOf("Cone 6 (1222°C)", "Cone 10 (1285°C)", "Cone 04 (1060°C)", "Cone 06 (1000°C)", "Raku")
  val locations = listOf("Shelf A-1", "Shelf A-2", "Shelf B-1", "Drying Rack 1", "Drying Rack 3", "Kiln Room", "Showroom Gallery", "Clay Storage")

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("dialog_add_edit_ceramic")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (item == null) "Add Ceramic Ware / Material" else "Edit Ceramic Item",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      if (errorMessage.isNotEmpty()) {
        Surface(
          color = Color(0xFFFEE2E2),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
        ) {
          Text(
            text = errorMessage,
            color = Color(0xFFB91C1C),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(10.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Item Name
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Item Name *") },
        placeholder = { Text("e.g., Speckled Stoneware Mug") },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_item_name")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // SKU with auto-generator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = sku,
          onValueChange = { sku = it },
          label = { Text("SKU / Item Code *") },
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("input_item_sku")
        )

        OutlinedButton(
          onClick = {
            val prefix = when (category) {
              "Tableware" -> "TBL"
              "Vases & Vessels" -> "VAS"
              "Planters & Pots" -> "PLT"
              "Sculptural" -> "ART"
              "Clay & Raw Materials" -> "CLY"
              else -> "GLZ"
            }
            sku = "CRM-$prefix-${Random.nextInt(10, 99)}"
          },
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.padding(top = 8.dp)
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Auto SKU", fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Category Selection
      Text("Category", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        categories.forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(cat, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = TerracottaPrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Stage Selection
      Text("Production / Firing Stage", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        stages.forEach { st ->
          FilterChip(
            selected = stage == st,
            onClick = { stage = st },
            label = { Text(st, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = CeladonSecondary,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Clay Body & Glaze
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = clayBody,
          onValueChange = { clayBody = it },
          label = { Text("Clay Body") },
          modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
          value = glazeType,
          onValueChange = { glazeType = it },
          label = { Text("Glaze Type") },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Firing Cone & Location
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = firingTemp,
          onValueChange = { firingTemp = it },
          label = { Text("Firing Temp / Cone") },
          modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
          value = location,
          onValueChange = { location = it },
          label = { Text("Storage Location") },
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Quantities: Current Stock & Minimum Threshold
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = quantityText,
          onValueChange = { quantityText = it },
          label = { Text("Current Stock *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .weight(1f)
            .testTag("input_item_quantity")
        )
        OutlinedTextField(
          value = minThresholdText,
          onValueChange = { minThresholdText = it },
          label = { Text("Low Alert Min *") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .weight(1f)
            .testTag("input_item_min_threshold")
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Financials: Unit Cost & Retail Selling Price
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = unitCostText,
          onValueChange = { unitCostText = it },
          label = { Text("Unit Cost ($)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier
            .weight(1f)
            .testTag("input_item_cost")
        )
        OutlinedTextField(
          value = unitPriceText,
          onValueChange = { unitPriceText = it },
          label = { Text("Retail Price ($)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier
            .weight(1f)
            .testTag("input_item_price")
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Notes
      OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        label = { Text("Studio Notes & Batch Details") },
        maxLines = 3,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDismiss,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("Cancel")
        }

        Button(
          onClick = {
            if (name.isBlank()) {
              errorMessage = "Item name cannot be empty"
              return@Button
            }
            val qty = quantityText.toIntOrNull()
            if (qty == null || qty < 0) {
              errorMessage = "Please enter a valid stock quantity"
              return@Button
            }
            val minTh = minThresholdText.toIntOrNull() ?: 0
            val cost = unitCostText.toDoubleOrNull() ?: 0.0
            val price = unitPriceText.toDoubleOrNull() ?: 0.0

            val newItem = CeramicItem(
              id = item?.id ?: 0L,
              name = name.trim(),
              sku = sku.trim(),
              category = category,
              stage = stage,
              clayBody = clayBody.trim(),
              glazeType = glazeType.trim(),
              quantity = qty,
              minThreshold = minTh,
              unitCost = cost,
              unitPrice = price,
              location = location.trim(),
              firingTemp = firingTemp.trim(),
              notes = notes.trim(),
              lastUpdated = System.currentTimeMillis()
            )
            onSave(newItem)
          },
          colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("btn_save_ceramic_item")
        ) {
          Text(if (item == null) "Add Item" else "Update Item")
        }
      }
    }
  }
}

@Composable
fun StockAdjustmentDialog(
  item: CeramicItem,
  onDismiss: () -> Unit,
  onConfirm: (delta: Int, reason: String) -> Unit
) {
  var isStockIn by remember { mutableStateOf(true) }
  var amountText by remember { mutableStateOf("1") }
  var selectedReason by remember { mutableStateOf("Kiln Unload Batch") }

  val inReasons = listOf("Kiln Unload Batch", "Restock Delivery", "Glaze Production", "Inventory Audit Correction")
  val outReasons = listOf("Showroom Sale", "Wholesale Order", "Breakage / Kiln Loss", "Glaze Defect", "Studio Sample")

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(16.dp),
    title = {
      Column {
        Text("Stock Adjustment", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(
          text = item.name,
          style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Current Stock: ${item.quantity} pcs",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Stock In vs Stock Out Tabs
        TabRow(selectedTabIndex = if (isStockIn) 0 else 1) {
          Tab(
            selected = isStockIn,
            onClick = {
              isStockIn = true
              selectedReason = inReasons.first()
            },
            text = { Text("Stock IN (+)", fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = !isStockIn,
            onClick = {
              isStockIn = false
              selectedReason = outReasons.first()
            },
            text = { Text("Stock OUT (-)", fontWeight = FontWeight.Bold) }
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quantity presets (+1, +5, +10, +25)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(1, 5, 10, 20).forEach { preset ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (amountText == preset.toString()) TerracottaPrimary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .weight(1f)
                .clickable { amountText = preset.toString() }
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 8.dp)
              ) {
                Text(
                  text = "+$preset",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (amountText == preset.toString()) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("Amount to ${if (isStockIn) "Add" else "Deduct"}") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_adjust_amount")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Reason / Batch Note",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))

        val currentReasons = if (isStockIn) inReasons else outReasons
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          currentReasons.forEach { reason ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (selectedReason == reason) CeladonSecondary.copy(alpha = 0.15f) else Color.Transparent,
              border = if (selectedReason == reason) androidx.compose.foundation.BorderStroke(1.dp, CeladonSecondary) else null,
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedReason = reason }
            ) {
              Text(
                text = reason,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = if (selectedReason == reason) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedReason == reason) CeladonSecondary else MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toIntOrNull() ?: 1
          val delta = if (isStockIn) amt else -amt
          onConfirm(delta, selectedReason)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isStockIn) StatusInStock else AlertOutOfStock
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("btn_confirm_stock_adjust")
      ) {
        Text(if (isStockIn) "Confirm Stock IN" else "Confirm Stock OUT")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailSheet(
  item: CeramicItem,
  onDismiss: () -> Unit,
  onEdit: () -> Unit,
  onAdjustStock: () -> Unit,
  onDelete: () -> Unit
) {
  val currency = NumberFormat.getCurrencyInstance(Locale.US)
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("sheet_item_detail")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 36.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.name,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${item.sku} • ${item.category}",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
          )
        }
        StockStatusBadge(item = item)
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Big Stock Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Stock On Hand", style = MaterialTheme.typography.labelSmall)
            Text(
              text = "${item.quantity} units",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = when {
                  item.isOutOfStock -> AlertOutOfStock
                  item.isLowStock -> AlertLowStock
                  else -> MaterialTheme.colorScheme.onSurface
                }
              )
            )
            Text(
              text = "Low alert threshold: ${item.minThreshold} pcs",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }

          Button(
            onClick = onAdjustStock,
            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("btn_detail_adjust_stock")
          ) {
            Text("± Adjust Stock")
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Technical & Material Details Grid
      Text(
        text = "Ceramic Specifications",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DetailRow(label = "Clay Body", value = item.clayBody)
        DetailRow(label = "Glaze Finish", value = item.glazeType)
        DetailRow(label = "Firing Temperature", value = item.firingTemp)
        DetailRow(label = "Production Stage", value = item.stage)
        DetailRow(label = "Studio Location", value = item.location)
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Valuation Breakdown
      Text(
        text = "Financial & Inventory Valuation",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DetailRow(label = "Unit Cost Basis", value = currency.format(item.unitCost))
        DetailRow(label = "Retail Selling Price", value = currency.format(item.unitPrice))
        DetailRow(label = "Unit Gross Profit", value = currency.format((item.unitPrice - item.unitCost).coerceAtLeast(0.0)))
        DetailRow(label = "Total Cost Asset", value = currency.format(item.totalCostValue))
        DetailRow(label = "Total Retail Valuation", value = currency.format(item.totalRetailValue))
      }

      if (item.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "Studio Notes",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = item.notes,
          style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Bottom Actions: Edit / Delete
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onDelete,
          colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertOutOfStock),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("btn_delete_item")
        ) {
          Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Delete")
        }

        Button(
          onClick = onEdit,
          colors = ButtonDefaults.buttonColors(containerColor = CeladonSecondary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("btn_edit_item")
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Edit")
        }
      }
    }
  }
}

@Composable
private fun DetailRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
    )
  }
}
