package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.TerracottaPrimary
import com.example.ui.theme.CeladonSecondary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CeramicsTopBar(
  onShareReport: () -> Unit,
  onOpenAdd: () -> Unit,
  modifier: Modifier = Modifier
) {
  TopAppBar(
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = TerracottaPrimary,
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text("🏺", fontSize = 18.sp)
          }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Atelier Ceramics",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = (-0.2).sp
            )
          )
          Text(
            text = "Stock Inventory & Valuation Report",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }
    },
    actions = {
      IconButton(
        onClick = onShareReport,
        modifier = Modifier.testTag("action_share_report")
      ) {
        Icon(
          imageVector = Icons.Default.Share,
          contentDescription = "Share Stock Report",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }
      IconButton(
        onClick = onOpenAdd,
        modifier = Modifier.testTag("action_add_item")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "Add Ceramic Item",
          tint = TerracottaPrimary
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface,
      titleContentColor = MaterialTheme.colorScheme.onSurface
    ),
    modifier = modifier.testTag("ceramics_top_bar")
  )
}

@Composable
fun CeramicsStudioHeroBanner(
  metrics: StockReportMetrics,
  onOpenReport: () -> Unit,
  modifier: Modifier = Modifier
) {
  val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .height(140.dp)
      .clip(RoundedCornerShape(16.dp))
      .testTag("ceramics_hero_banner")
  ) {
    // Background Image
    Image(
      painter = painterResource(id = R.drawable.img_ceramics_banner),
      contentDescription = "Ceramics Studio Banner",
      contentScale = ContentScale.Crop,
      modifier = Modifier.matchParentSize()
    )

    // Gradient Overlay for readability
    Box(
      modifier = Modifier
        .matchParentSize()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              Color(0xEE1E1714),
              Color(0x992B211C),
              Color(0x442B211C)
            )
          )
        )
    )

    // Content Overlay
    Column(
      modifier = Modifier
        .matchParentSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = CeladonSecondary.copy(alpha = 0.85f),
            modifier = Modifier.padding(bottom = 4.dp)
          ) {
            Text(
              text = "STUDIO LIVE INVENTORY",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Text(
            text = "Handcrafted Pottery Stock",
            style = MaterialTheme.typography.titleMedium.copy(
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          )
        }

        if (metrics.lowStockCount > 0 || metrics.outOfStockCount > 0) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFD97706),
            modifier = Modifier.padding(start = 4.dp)
          ) {
            Text(
              text = "⚠️ ${metrics.lowStockCount + metrics.outOfStockCount} Alerts",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
              ),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Quick Stats Footer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
          Column {
            Text(
              text = "Total Stock",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD3CBC4))
            )
            Text(
              text = "${metrics.totalItemsCount} pcs",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            )
          }
          Column {
            Text(
              text = "Catalog SKUs",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD3CBC4))
            )
            Text(
              text = "${metrics.totalSkus} items",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            )
          }
          Column {
            Text(
              text = "Retail Valuation",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD3CBC4))
            )
            Text(
              text = currencyFormatter.format(metrics.totalRetailValue),
              style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFFFDE68A),
                fontWeight = FontWeight.Bold
              )
            )
          }
        }
      }
    }
  }
}
