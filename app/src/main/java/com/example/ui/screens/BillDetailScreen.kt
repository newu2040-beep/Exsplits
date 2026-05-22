package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.util.ExportUtils
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailScreen(
    id: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val bills by viewModel.allBills.collectAsStateWithLifecycle()
    val bill = bills.find { it.id == id }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exportType by remember { mutableStateOf("") }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        uri?.let {
            if (bill != null) {
                scope.launch {
                    when (exportType) {
                        "json" -> ExportUtils.exportJson(context, uri, bill)
                        "text" -> ExportUtils.exportText(context, uri, bill)
                        "pdf" -> ExportUtils.exportPdf(context, uri, bill)
                    }
                }
            }
        }
    }

    if (bill == null) {
        onBack()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bill.title, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(bill.id, !bill.isFavorite) }) {
                        Icon(
                            if (bill.isFavorite) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Favorite",
                            tint = if (bill.isFavorite) Color(0xFFFFC107) else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = {
                        viewModel.deleteBill(bill.id)
                        onBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Invoice Card View
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, CircleShape).padding(12.dp)) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("INVOICE", fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.primary)
                        Text("#${bill.id.take(8).uppercase()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${bill.currency} ${String.format("%.2f", bill.amount)}",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(bill.date))
                val payer = bill.participants.find { it.id == bill.paidBy }?.name ?: "Unknown"

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Category", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(bill.category, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Date", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(dateStr, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Paid By", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(payer, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Split Type", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(bill.splitType.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Dashed Line Divider
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                Text("SPLIT BREAKDOWN", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    bill.splitData.forEach { (pid, amount) ->
                        val p = bill.participants.find { it.id == pid }?.name ?: pid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(p, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("${bill.currency} ${String.format("%.2f", amount)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Export Actions Grid
            Text("Export & Share tools", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExportActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.ContentCopy,
                    label = "Copy Text",
                    onClick = {
                        val text = "Bill: ${bill.title}\nAmount: ${bill.currency} ${bill.amount}\nDate: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(bill.date))}\nPaid by: ${bill.participants.find { it.id == bill.paidBy }?.name}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Bill Details", text)
                        clipboard.setPrimaryClip(clip)
                    }
                )
                ExportActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.PictureAsPdf,
                    label = "Export PDF",
                    onClick = {
                        exportType = "pdf"
                        createDocumentLauncher.launch("${bill.title}.pdf")
                    }
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExportActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Code,
                    label = "Save JSON",
                    onClick = {
                        exportType = "json"
                        createDocumentLauncher.launch("${bill.title}.json")
                    }
                )
                ExportActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.ReceiptLong,
                    label = "Save Text",
                    onClick = {
                        exportType = "text"
                        createDocumentLauncher.launch("${bill.title}.txt")
                    }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ExportActionCard(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

