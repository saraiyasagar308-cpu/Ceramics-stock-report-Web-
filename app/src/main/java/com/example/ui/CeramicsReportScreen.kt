package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.StockTransaction
import com.example.ui.theme.AlertLowStock
import com.example.ui.theme.AlertOutOfStock
import com.example.ui.theme.CeladonSecondary
import com.example.ui.theme.OchreGlazeTertiary
import com.example.ui.theme.TerracottaPrimary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CeramicsReportScreen(
  metrics: StockReportMetrics,
  items: List<CeramicItem>,
  transactions: List<StockTransaction>,
  onSwitchToWebView: () -> Unit,
  onShareReport: () -> Unit,
  onCopyHtml: () -> Unit,
  onItemClick: (CeramicItem) -> Unit,
  modifier: Modifier = Modifier
) {
  val currency = NumberFormat.getCurrencyInstance(Locale.US)
  val lowStockList = items.filter { it.isLowStock || it.isOutOfStock }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("ceramics_report_screen"),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Executive Summary Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = TerracottaPrimary
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_report_executive_summary")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "EXECUTIVE VALUATION REPORT",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFFFFDBCF),
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
              )
              Text(
                text = "Total Studio Valuation",
                style = MaterialTheme.typography.titleLarge.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.2f)
            ) {
              Text(
                text = "${metrics.totalItemsCount} pcs",
                style = MaterialTheme.typography.labelMedium.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Key Values Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = "Retail Market Value",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFFDBCF))
              )
              Text(
                text = currency.format(metrics.totalRetailValue),
                style = MaterialTheme.typography.headlineSmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.ExtraBold
                )
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "Cost Basis",
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFFDBCF))
              )
              Text(
                text = currency.format(metrics.totalCostValue),
                style = MaterialTheme.typography.titleMedium.copy(
                  color = Color(0xFFFEE2E2),
                  fontWeight = FontWeight.SemiBold
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color(0xFF86EFAC),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Gross Margin: ${currency.format(metrics.totalGrossMargin)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Medium
                )
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFF15803D).copy(alpha = 0.4f)
            ) {
              Text(
                text = "${String.format(Locale.US, "%.1f", metrics.marginPercentage)}% margin",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF86EFAC),
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }
      }
    }

    // Web Site Report Action Banner
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CeladonSecondary.copy(alpha = 0.15f),
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Language,
                  contentDescription = null,
                  tint = CeladonSecondary
                )
              }
            }
            Column {
              Text(
                text = "Ceramics Stock Web Report",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Interactive, responsive HTML web site report",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onSwitchToWebView,
              colors = ButtonDefaults.buttonColors(containerColor = CeladonSecondary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("btn_view_web_report")
            ) {
              Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("View Web Site", fontSize = 13.sp)
            }

            OutlinedButton(
              onClick = onShareReport,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("btn_share_web_report")
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Share", fontSize = 13.sp)
            }

            OutlinedButton(
              onClick = onCopyHtml,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("btn_copy_html_report")
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    }

    // Critical Restock / Low Stock Section
    if (lowStockList.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFBEB)
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = null,
                  tint = AlertLowStock,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Priority Restock & Kiln Attention (${lowStockList.size})",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            lowStockList.forEach { lowItem ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { onItemClick(lowItem) }
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = lowItem.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                  )
                  Text(
                    text = "${lowItem.sku} • Location: ${lowItem.location}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF78350F))
                  )
                }

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = if (lowItem.isOutOfStock) AlertOutOfStock.copy(alpha = 0.15f) else AlertLowStock.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = if (lowItem.isOutOfStock) "DEPLETED" else "${lowItem.quantity}/${lowItem.minThreshold} min",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (lowItem.isOutOfStock) AlertOutOfStock else AlertLowStock
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Category Breakdown Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Category Distribution & Valuation",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(14.dp))

          metrics.categoryBreakdown.forEach { (cat, catMetric) ->
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = cat,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                  text = "${catMetric.totalUnits} pcs  •  ${currency.format(catMetric.retailValue)}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                  )
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              LinearProgressIndicator(
                progress = { (catMetric.sharePercentage / 100f).toFloat().coerceIn(0f, 1f) },
                color = when (cat) {
                  "Tableware" -> TerracottaPrimary
                  "Vases & Vessels" -> CeladonSecondary
                  "Planters & Pots" -> OchreGlazeTertiary
                  else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp))
              )
            }
          }
        }
      }
    }

    // Production Stage Breakdown Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = OchreGlazeTertiary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Firing & Production Stages",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            metrics.stageBreakdown.forEach { (stage, count) ->
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp)
              ) {
                Text(
                  text = "$count",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TerracottaPrimary
                  )
                )
                Text(
                  text = stage.split("/").first().trim(),
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                  ),
                  maxLines = 1
                )
              }
            }
          }
        }
      }
    }

    // Recent Activity / Transactions Log
    if (transactions.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Recent Stock Adjustments & Kiln Activity",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val timeFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())

            transactions.take(6).forEach { tx ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = tx.itemName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                  )
                  Text(
                    text = "${tx.reason} • ${timeFormat.format(Date(tx.timestamp))}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (tx.changeAmount >= 0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                ) {
                  Text(
                    text = if (tx.changeAmount >= 0) "+${tx.changeAmount}" else "${tx.changeAmount}",
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (tx.changeAmount >= 0) Color(0xFF15803D) else Color(0xFFB91C1C)
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                  )
                }
              }
              HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            }
          }
        }
      }
    }
  }
}
