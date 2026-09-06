package com.example.report

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.CeramicItem
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WebReportGenerator {

  private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
  private val dateFormatter = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

  fun generateHtmlReport(
    items: List<CeramicItem>,
    studioName: String = "Atelier Ceramics Studio"
  ): String {
    val totalItemsCount = items.sumOf { it.quantity }
    val totalSkus = items.size
    val totalCostValue = items.sumOf { it.totalCostValue }
    val totalRetailValue = items.sumOf { it.totalRetailValue }
    val totalGrossMargin = (totalRetailValue - totalCostValue).coerceAtLeast(0.0)
    val marginPercentage = if (totalRetailValue > 0) (totalGrossMargin / totalRetailValue * 100) else 0.0
    val lowStockCount = items.count { it.isLowStock }
    val outOfStockCount = items.count { it.isOutOfStock }
    val reportDate = dateFormatter.format(Date())

    // Category breakdown
    val categoryGroups = items.groupBy { it.category }
    // Stage breakdown
    val stageGroups = items.groupBy { it.stage }

    val categoryRowsHtml = categoryGroups.map { (cat, catItems) ->
      val count = catItems.sumOf { it.quantity }
      val cost = catItems.sumOf { it.totalCostValue }
      val retail = catItems.sumOf { it.totalRetailValue }
      val pct = if (totalItemsCount > 0) (count.toDouble() / totalItemsCount * 100) else 0.0
      """
      <tr>
        <td><strong>$cat</strong></td>
        <td>${catItems.size} SKUs</td>
        <td>$count pcs</td>
        <td>
          <div class="progress-bar-bg">
            <div class="progress-bar-fill" style="width: ${String.format(Locale.US, "%.1f", pct)}%;"></div>
          </div>
          <span class="pct-text">${String.format(Locale.US, "%.1f", pct)}%</span>
        </td>
        <td>${currencyFormatter.format(cost)}</td>
        <td><strong>${currencyFormatter.format(retail)}</strong></td>
      </tr>
      """.trimIndent()
    }.joinToString("\n")

    val inventoryRowsHtml = items.sortedBy { it.name }.map { item ->
      val statusClass = when {
        item.isOutOfStock -> "status-out"
        item.isLowStock -> "status-low"
        else -> "status-ok"
      }
      val statusText = when {
        item.isOutOfStock -> "DEPLETED"
        item.isLowStock -> "LOW (${item.quantity}/${item.minThreshold})"
        else -> "IN STOCK"
      }
      """
      <tr>
        <td>
          <strong>${escapeHtml(item.name)}</strong>
          <div class="meta-sub">SKU: ${escapeHtml(item.sku)} | Loc: ${escapeHtml(item.location)}</div>
        </td>
        <td><span class="badge badge-cat">${escapeHtml(item.category)}</span></td>
        <td>${escapeHtml(item.clayBody)} / ${escapeHtml(item.glazeType)}</td>
        <td><span class="badge badge-stage">${escapeHtml(item.stage)}</span></td>
        <td class="num"><strong>${item.quantity}</strong></td>
        <td><span class="status-pill $statusClass">$statusText</span></td>
        <td class="num">${currencyFormatter.format(item.unitCost)}</td>
        <td class="num">${currencyFormatter.format(item.unitPrice)}</td>
        <td class="num font-bold">${currencyFormatter.format(item.totalRetailValue)}</td>
      </tr>
      """.trimIndent()
    }.joinToString("\n")

    val lowStockRowsHtml = items.filter { it.isLowStock || it.isOutOfStock }.map { item ->
      """
      <div class="alert-card">
        <div class="alert-header">
          <strong>${escapeHtml(item.name)}</strong>
          <span class="sku-tag">${escapeHtml(item.sku)}</span>
        </div>
        <div class="alert-detail">
          Current Stock: <span class="alert-qty">${item.quantity}</span> (Min Threshold: ${item.minThreshold}) | Location: ${escapeHtml(item.location)} | Stage: ${escapeHtml(item.stage)}
        </div>
      </div>
      """.trimIndent()
    }.joinToString("\n")

    return """
    <!DOCTYPE html>
    <html lang="en">
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Ceramics Stock & Valuation Report - $studioName</title>
      <style>
        :root {
          --terracotta: #B85D3B;
          --terracotta-dark: #8C3E22;
          --celadon: #4A7C6D;
          --sand-bg: #FAF7F2;
          --card-bg: #FFFFFF;
          --text-main: #2C2420;
          --text-muted: #736760;
          --border-color: #E6DDD5;
          --status-ok: #16A34A;
          --status-low: #D97706;
          --status-out: #DC2626;
        }
        * {
          box-sizing: border-box;
          margin: 0;
          padding: 0;
        }
        body {
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
          background-color: var(--sand-bg);
          color: var(--text-main);
          line-height: 1.5;
          padding: 24px;
        }
        .container {
          max-width: 1100px;
          margin: 0 auto;
        }
        header {
          display: flex;
          flex-wrap: wrap;
          justify-content: space-between;
          align-items: center;
          padding-bottom: 20px;
          border-bottom: 2px solid var(--border-color);
          margin-bottom: 24px;
        }
        .brand-title {
          font-size: 28px;
          font-weight: 700;
          color: var(--terracotta);
          letter-spacing: -0.5px;
        }
        .brand-subtitle {
          font-size: 14px;
          color: var(--text-muted);
          margin-top: 4px;
        }
        .report-meta {
          text-align: right;
          font-size: 13px;
          color: var(--text-muted);
        }
        .badge-live {
          display: inline-block;
          background: #E8F5E9;
          color: #2E7D32;
          padding: 3px 8px;
          border-radius: 12px;
          font-size: 11px;
          font-weight: 600;
          margin-bottom: 4px;
        }
        /* KPI Cards Grid */
        .kpi-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
          gap: 16px;
          margin-bottom: 28px;
        }
        .kpi-card {
          background: var(--card-bg);
          padding: 18px;
          border-radius: 12px;
          border: 1px solid var(--border-color);
          box-shadow: 0 2px 8px rgba(0,0,0,0.04);
        }
        .kpi-label {
          font-size: 12px;
          text-transform: uppercase;
          color: var(--text-muted);
          letter-spacing: 0.5px;
          font-weight: 600;
        }
        .kpi-value {
          font-size: 24px;
          font-weight: 700;
          color: var(--text-main);
          margin: 6px 0 2px;
        }
        .kpi-sub {
          font-size: 12px;
          color: var(--text-muted);
        }
        .kpi-accent-terracotta .kpi-value { color: var(--terracotta); }
        .kpi-accent-celadon .kpi-value { color: var(--celadon); }
        .kpi-accent-amber .kpi-value { color: var(--status-low); }

        /* Section Layout */
        .section-title {
          font-size: 18px;
          font-weight: 700;
          color: var(--text-main);
          margin-bottom: 14px;
          display: flex;
          align-items: center;
          gap: 8px;
        }
        .section-box {
          background: var(--card-bg);
          border-radius: 12px;
          border: 1px solid var(--border-color);
          padding: 20px;
          margin-bottom: 28px;
          box-shadow: 0 2px 8px rgba(0,0,0,0.03);
          overflow-x: auto;
        }

        /* Tables */
        table {
          width: 100%;
          border-collapse: collapse;
          font-size: 13.5px;
          text-align: left;
        }
        th {
          background: #F8F5F0;
          padding: 10px 14px;
          font-weight: 600;
          color: var(--text-muted);
          border-bottom: 1px solid var(--border-color);
          white-space: nowrap;
        }
        td {
          padding: 12px 14px;
          border-bottom: 1px solid var(--border-color);
          vertical-align: middle;
        }
        tr:last-child td {
          border-bottom: none;
        }
        .num {
          text-align: right;
        }
        .font-bold {
          font-weight: 700;
        }
        .meta-sub {
          font-size: 11px;
          color: var(--text-muted);
          margin-top: 2px;
        }

        /* Badges & Status */
        .badge {
          display: inline-block;
          padding: 3px 8px;
          border-radius: 6px;
          font-size: 11px;
          font-weight: 500;
        }
        .badge-cat {
          background: #F0EDE8;
          color: #5C524B;
        }
        .badge-stage {
          background: #EAF0EE;
          color: var(--celadon);
        }
        .status-pill {
          display: inline-block;
          padding: 3px 8px;
          border-radius: 12px;
          font-size: 11px;
          font-weight: 700;
          letter-spacing: 0.3px;
        }
        .status-ok {
          background: #DCFCE7;
          color: #15803D;
        }
        .status-low {
          background: #FEF3C7;
          color: #B45309;
        }
        .status-out {
          background: #FEE2E2;
          color: #B91C1C;
        }

        /* Progress bars */
        .progress-bar-bg {
          width: 80px;
          height: 6px;
          background: #EAE3DC;
          border-radius: 3px;
          display: inline-block;
          vertical-align: middle;
          margin-right: 6px;
        }
        .progress-bar-fill {
          height: 100%;
          background: var(--terracotta);
          border-radius: 3px;
        }
        .pct-text {
          font-size: 11px;
          color: var(--text-muted);
        }

        /* Low Stock Alerts */
        .alert-card {
          background: #FFFBEB;
          border-left: 4px solid var(--status-low);
          padding: 12px 16px;
          border-radius: 6px;
          margin-bottom: 10px;
          border-top: 1px solid #FEF3C7;
          border-right: 1px solid #FEF3C7;
          border-bottom: 1px solid #FEF3C7;
        }
        .alert-header {
          display: flex;
          justify-content: space-between;
          font-size: 14px;
        }
        .sku-tag {
          font-size: 11px;
          color: var(--text-muted);
          background: #FEF3C7;
          padding: 2px 6px;
          border-radius: 4px;
        }
        .alert-detail {
          font-size: 12.5px;
          color: #92400E;
          margin-top: 4px;
        }
        .alert-qty {
          font-weight: 700;
          color: #B45309;
        }

        footer {
          text-align: center;
          padding: 24px 0 12px;
          font-size: 12px;
          color: var(--text-muted);
          border-top: 1px solid var(--border-color);
        }

        @media print {
          body {
            background: white;
            padding: 0;
          }
          .section-box {
            box-shadow: none;
            border: 1px solid #ccc;
          }
        }
      </style>
    </head>
    <body>
      <div class="container">
        <header>
          <div>
            <div class="brand-title">🏺 $studioName</div>
            <div class="brand-subtitle">Official Ceramics Inventory & Stock Valuation Web Report</div>
          </div>
          <div class="report-meta">
            <span class="badge-live">LIVE STUDIO REPORT</span>
            <div>Generated: <strong>$reportDate</strong></div>
            <div>Inventory Status: <strong>Active</strong></div>
          </div>
        </header>

        <!-- KPI Metrics -->
        <div class="kpi-grid">
          <div class="kpi-card kpi-accent-terracotta">
            <div class="kpi-label">Total Stock On Hand</div>
            <div class="kpi-value">$totalItemsCount pcs</div>
            <div class="kpi-sub">Across $totalSkus registered SKUs</div>
          </div>
          <div class="kpi-card">
            <div class="kpi-label">Cost Valuation</div>
            <div class="kpi-value">${currencyFormatter.format(totalCostValue)}</div>
            <div class="kpi-sub">Raw & production cost</div>
          </div>
          <div class="kpi-card kpi-accent-celadon">
            <div class="kpi-label">Retail Market Value</div>
            <div class="kpi-value">${currencyFormatter.format(totalRetailValue)}</div>
            <div class="kpi-sub">Potential studio revenue</div>
          </div>
          <div class="kpi-card">
            <div class="kpi-label">Gross Margin</div>
            <div class="kpi-value">${currencyFormatter.format(totalGrossMargin)}</div>
            <div class="kpi-sub">${String.format(Locale.US, "%.1f", marginPercentage)}% potential markup</div>
          </div>
          <div class="kpi-card ${if (lowStockCount > 0 || outOfStockCount > 0) "kpi-accent-amber" else ""}">
            <div class="kpi-label">Stock Alerts</div>
            <div class="kpi-value">${lowStockCount + outOfStockCount} items</div>
            <div class="kpi-sub">$lowStockCount low stock, $outOfStockCount out of stock</div>
          </div>
        </div>

        ${if (lowStockCount > 0 || outOfStockCount > 0) """
        <div class="section-title">⚠️ Priority Restock & Kiln Attention Required</div>
        <div class="section-box" style="padding: 14px;">
          $lowStockRowsHtml
        </div>
        """ else ""}

        <!-- Category Breakdown -->
        <div class="section-title">📊 Category Distribution & Valuation</div>
        <div class="section-box">
          <table>
            <thead>
              <tr>
                <th>Category</th>
                <th>Catalog Size</th>
                <th>In Stock</th>
                <th>Volume Share</th>
                <th>Cost Basis</th>
                <th>Retail Value</th>
              </tr>
            </thead>
            <tbody>
              $categoryRowsHtml
            </tbody>
          </table>
        </div>

        <!-- Master Inventory Table -->
        <div class="section-title">📦 Complete Ceramic Ware & Material Stock Roster</div>
        <div class="section-box">
          <table>
            <thead>
              <tr>
                <th>Item & Details</th>
                <th>Category</th>
                <th>Clay / Glaze</th>
                <th>Stage</th>
                <th class="num">Stock</th>
                <th>Status</th>
                <th class="num">Cost</th>
                <th class="num">Price</th>
                <th class="num">Total Asset</th>
              </tr>
            </thead>
            <tbody>
              $inventoryRowsHtml
            </tbody>
          </table>
        </div>

        <footer>
          $studioName • Ceramics Inventory Management System • Generated automatically for studio staff & showroom
        </footer>
      </div>
    </body>
    </html>
    """.trimIndent()
  }

  fun shareWebReport(context: Context, html: String, items: List<CeramicItem>) {
    try {
      val file = File(context.cacheDir, "ceramics_stock_report.html")
      file.writeText(html)

      val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )

      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/html"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Ceramics Stock & Valuation Report")
        putExtra(
          Intent.EXTRA_TEXT,
          "Ceramics Stock Report\nTotal Items: ${items.sumOf { it.quantity }} pcs\nTotal Retail Value: ${currencyFormatter.format(items.sumOf { it.totalRetailValue })}\nGenerated from Atelier Ceramics Studio."
        )
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(shareIntent, "Share Ceramics Stock Report"))
    } catch (e: Exception) {
      // Fallback to plain text sharing if FileProvider has an issue
      val textIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Ceramics Stock & Valuation Report")
        putExtra(
          Intent.EXTRA_TEXT,
          generateTextSummary(items)
        )
      }
      context.startActivity(Intent.createChooser(textIntent, "Share Ceramics Stock Report"))
    }
  }

  fun copyHtmlToClipboard(context: Context, html: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Ceramics Stock Report HTML", html)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Stock Report HTML copied to clipboard!", Toast.LENGTH_SHORT).show()
  }

  fun generateTextSummary(items: List<CeramicItem>): String {
    val totalCount = items.sumOf { it.quantity }
    val totalCost = items.sumOf { it.totalCostValue }
    val totalRetail = items.sumOf { it.totalRetailValue }
    val lowStock = items.filter { it.isLowStock || it.isOutOfStock }

    return buildString {
      appendLine("=== CERAMICS STOCK & VALUATION REPORT ===")
      appendLine("Generated: ${dateFormatter.format(Date())}")
      appendLine("Total SKUs: ${items.size}")
      appendLine("Total Stock: $totalCount pcs")
      appendLine("Total Cost Basis: ${currencyFormatter.format(totalCost)}")
      appendLine("Total Retail Valuation: ${currencyFormatter.format(totalRetail)}")
      appendLine()
      if (lowStock.isNotEmpty()) {
        appendLine("--- LOW / DEPLETED STOCK ALERTS ---")
        lowStock.forEach {
          appendLine("• ${it.name} (${it.sku}): ${it.quantity} in stock (Min: ${it.minThreshold})")
        }
        appendLine()
      }
      appendLine("--- INVENTORY ROSTER ---")
      items.sortedBy { it.name }.forEach {
        appendLine("• ${it.name} [${it.sku}] - Qty: ${it.quantity} | ${it.category} | ${currencyFormatter.format(it.unitPrice)}")
      }
    }
  }

  private fun escapeHtml(text: String): String {
    return text.replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#39;")
  }
}
