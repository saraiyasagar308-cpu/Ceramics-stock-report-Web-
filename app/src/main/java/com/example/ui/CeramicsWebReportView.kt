package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.CeramicItem
import com.example.report.WebReportGenerator
import com.example.ui.theme.AlertLowStock
import com.example.ui.theme.AlertOutOfStock
import com.example.ui.theme.CeladonSecondary
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.StonewareSandLight
import com.example.ui.theme.StatusInStock
import com.example.ui.theme.TerracottaPrimary
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CeramicsWebReportView(
  items: List<CeramicItem>,
  onShareReport: () -> Unit,
  onCopyHtml: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val htmlReport = remember(items) {
    WebReportGenerator.generateHtmlReport(items)
  }

  var isViewingHtmlSource by remember { mutableStateOf(false) }

  val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale.US) }
  val totalItemsCount = items.sumOf { it.quantity }
  val totalSkus = items.size
  val totalCostValue = items.sumOf { it.totalCostValue }
  val totalRetailValue = items.sumOf { it.totalRetailValue }
  val grossProfit = (totalRetailValue - totalCostValue).coerceAtLeast(0.0)
  val marginPct = if (totalRetailValue > 0) (grossProfit / totalRetailValue * 100) else 0.0

  val lowOrOutItems = items.filter { it.isLowStock || it.isOutOfStock }
  val categoryGroups = items.groupBy { it.category }
  val stageGroups = items.groupBy { it.stage }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF1ECE4))
      .testTag("ceramics_web_report_view")
  ) {
    // Browser Mockup Header & Address Bar
    Surface(
      color = Color(0xFF2C2420),
      tonalElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
        // Window Control Dots + Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEF4444)))
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Atelier Ceramics Studio — Stock Report Website",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFFD6CECA),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            )
          }

          Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(0xFF15803D),
            modifier = Modifier.padding(start = 6.dp)
          ) {
            Text(
              text = "● LIVE WEB REPORT",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Browser URL Address Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF3F3530))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Secure HTTPS",
              tint = Color(0xFF10B981),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "https://atelierceramics.studio/stock-report.html",
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFFAF7F2),
                fontSize = 12.sp
              ),
              maxLines = 1
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Open in external browser
            IconButton(
              onClick = { openInExternalBrowser(context, htmlReport) },
              modifier = Modifier.size(28.dp).testTag("btn_open_external_browser")
            ) {
              Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = "Open in Web Browser",
                tint = TerracottaPrimary,
                modifier = Modifier.size(18.dp)
              )
            }

            // Share HTML
            IconButton(
              onClick = onShareReport,
              modifier = Modifier.size(28.dp).testTag("btn_share_html_web")
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share HTML",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }

            // Copy HTML
            IconButton(
              onClick = onCopyHtml,
              modifier = Modifier.size(28.dp).testTag("btn_copy_html_web")
            ) {
              Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy HTML Code",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
            }

            // Toggle Source / Web Preview
            IconButton(
              onClick = { isViewingHtmlSource = !isViewingHtmlSource },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = if (isViewingHtmlSource) Icons.Default.Visibility else Icons.Default.Code,
                contentDescription = if (isViewingHtmlSource) "Preview View" else "HTML Code",
                tint = CeladonSecondary,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }

    // Main Web Viewport (Clean, high-fidelity responsive web report rendered in native Compose)
    Box(
      modifier = Modifier
        .fillMaxSize()
        .testTag("webview_ceramics_stock_report")
    ) {
      if (isViewingHtmlSource) {
        // Raw HTML Inspector Mode
        Column(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "HTML Source Code (Standalone Website)",
              style = MaterialTheme.typography.titleSmall.copy(
                color = Color(0xFFE2E8F0),
                fontWeight = FontWeight.Bold
              )
            )
            Button(
              onClick = onCopyHtml,
              colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text("Copy All HTML", fontSize = 12.sp)
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = htmlReport,
            style = MaterialTheme.typography.bodySmall.copy(
              fontFamily = FontFamily.Monospace,
              color = Color(0xFF86EFAC),
              fontSize = 11.sp,
              lineHeight = 16.sp
            )
          )
        }
      } else {
        // Live Rendered Web Page View
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 60.dp)
        ) {
          // Website Hero Branding Card
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "ATELIER CERAMICS STUDIO",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = TerracottaPrimary,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.sp
                    )
                  )
                  Text(
                    text = "Stock & Valuation Web Report",
                    style = MaterialTheme.typography.headlineSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = CharcoalText
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Live inventory report generated on ${SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
              )

              Spacer(modifier = Modifier.height(12.dp))
              HorizontalDivider(color = Color(0xFFE5E0DA))
              Spacer(modifier = Modifier.height(12.dp))

              // 4 Key Performance Indicators
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WebKpiTile(
                  label = "TOTAL PIECES",
                  value = "$totalItemsCount",
                  subtext = "$totalSkus SKUs",
                  color = TerracottaPrimary,
                  modifier = Modifier.weight(1f)
                )
                WebKpiTile(
                  label = "RETAIL VALUATION",
                  value = currencyFormatter.format(totalRetailValue),
                  subtext = "Potential Revenue",
                  color = Color(0xFF15803D),
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WebKpiTile(
                  label = "COST BASIS ASSET",
                  value = currencyFormatter.format(totalCostValue),
                  subtext = "Raw materials & firing",
                  color = CeladonSecondary,
                  modifier = Modifier.weight(1f)
                )
                WebKpiTile(
                  label = "GROSS MARGIN",
                  value = String.format(Locale.US, "%.1f%%", marginPct),
                  subtext = "${currencyFormatter.format(grossProfit)} profit",
                  color = Color(0xFF7C3AED),
                  modifier = Modifier.weight(1f)
                )
              }
            }
          }

          // Low Stock Alert Web Card (if any)
          if (lowOrOutItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0xFFFFFBEB),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "⚠ Studio Restock Alerts (${lowOrOutItems.size} items require attention)",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF92400E)
                    )
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
                lowOrOutItems.forEach { item ->
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodySmall.copy(
                          fontWeight = FontWeight.SemiBold,
                          color = Color(0xFF78350F)
                        )
                      )
                      Text(
                        text = "${item.sku} • Location: ${item.location} • Stage: ${item.stage}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB45309), fontSize = 11.sp)
                      )
                    }
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = if (item.isOutOfStock) Color(0xFFFEE2E2) else Color(0xFFFEF3C7)
                    ) {
                      Text(
                        text = if (item.isOutOfStock) "DEPLETED" else "LOW: ${item.quantity}/${item.minThreshold}",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = if (item.isOutOfStock) Color(0xFFB91C1C) else Color(0xFF92400E),
                          fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }

          // Category Breakdown Web Table
          Spacer(modifier = Modifier.height(16.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
              Text(
                text = "Inventory by Category",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = CharcoalText
                )
              )
              Spacer(modifier = Modifier.height(8.dp))

              categoryGroups.forEach { (cat, catItems) ->
                val qty = catItems.sumOf { it.quantity }
                val retail = catItems.sumOf { it.totalRetailValue }
                val pct = if (totalItemsCount > 0) qty.toFloat() / totalItemsCount else 0f

                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(text = cat, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                    Text(
                      text = "$qty pcs • ${currencyFormatter.format(retail)}",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                      )
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  LinearProgressIndicator(
                    progress = { pct },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = TerracottaPrimary,
                    trackColor = Color(0xFFF0ECE7)
                  )
                }
              }
            }
          }

          // Complete Inventory Roster Web Table
          Spacer(modifier = Modifier.height(16.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Studio Ware Master List",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText
                  )
                )
                Text(
                  text = "${items.size} Ware SKUs",
                  style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Table header
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(Color(0xFFFAF7F2), RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("ITEM & CODE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.8f))
                Text("STAGE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.2f))
                Text("QTY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(0.7f))
                Text("VALUATION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.3f))
              }

              Spacer(modifier = Modifier.height(4.dp))

              items.forEachIndexed { index, item ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1.8f)) {
                    Text(
                      text = item.name,
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                      maxLines = 1
                    )
                    Text(
                      text = "${item.sku} • ${item.clayBody}",
                      style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
                    )
                  }

                  Text(
                    text = item.stage,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    modifier = Modifier.weight(1.2f),
                    maxLines = 1
                  )

                  Text(
                    text = "${item.quantity}",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = when {
                        item.isOutOfStock -> AlertOutOfStock
                        item.isLowStock -> AlertLowStock
                        else -> CharcoalText
                      }
                    ),
                    modifier = Modifier.weight(0.7f)
                  )

                  Column(modifier = Modifier.weight(1.3f), horizontalAlignment = Alignment.End) {
                    Text(
                      text = currencyFormatter.format(item.totalRetailValue),
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "@ ${currencyFormatter.format(item.unitPrice)}",
                      style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
                    )
                  }
                }
                if (index < items.size - 1) {
                  HorizontalDivider(color = Color(0xFFF3EFEA))
                }
              }
            }
          }

          // Browser Actions Call to Action Card
          Spacer(modifier = Modifier.height(16.dp))
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF2C2420),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Export & Open Website",
                style = MaterialTheme.typography.titleSmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Open in Chrome, download the HTML report file, or share with studio partners.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFFD6CECA),
                  fontSize = 12.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
              Spacer(modifier = Modifier.height(12.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Button(
                  onClick = { openInExternalBrowser(context, htmlReport) },
                  colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Open in Browser", fontSize = 12.sp)
                }

                Button(
                  onClick = onShareReport,
                  colors = ButtonDefaults.buttonColors(containerColor = CeladonSecondary),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Share HTML", fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun WebKpiTile(
  label: String,
  value: String,
  subtext: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = color.copy(alpha = 0.08f),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f)),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          color = color,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp,
          letterSpacing = 0.5.sp
        )
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = CharcoalText
        )
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtext,
        style = MaterialTheme.typography.bodySmall.copy(
          color = Color.Gray,
          fontSize = 10.sp
        )
      )
    }
  }
}

private fun openInExternalBrowser(context: Context, html: String) {
  try {
    val file = File(context.cacheDir, "ceramics_stock_report.html")
    file.writeText(html)
    val uri = FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      file
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
      setDataAndType(uri, "text/html")
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(Intent.createChooser(intent, "Open Ceramics Stock Report"))
  } catch (e: Exception) {
    Toast.makeText(context, "Could not open browser. Share or copy HTML report instead.", Toast.LENGTH_SHORT).show()
  }
}
