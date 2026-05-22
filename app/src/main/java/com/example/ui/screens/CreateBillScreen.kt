package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DomainBill
import com.example.data.Participant
import com.example.data.SplitType
import com.example.viewmodel.MainViewModel
import java.util.Currency
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBillScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    
    val allCurrencies = remember { Currency.getAvailableCurrencies().toList().sortedBy { it.currencyCode } }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var expandCurrency by remember { mutableStateOf(false) }

    var splitType by remember { mutableStateOf(SplitType.EQUAL) }
    var expandSplitType by remember { mutableStateOf(false) }

    val participants = remember { mutableStateListOf<Participant>(Participant(UUID.randomUUID().toString(), "You")) }
    var newParticipantName by remember { mutableStateOf("") }

    var selectedPayerId by remember { mutableStateOf(participants.first().id) }
    var expandPayer by remember { mutableStateOf(false) }

    // Categories
    val categories = listOf("Dining", "Groceries", "Travel", "Utility", "Rent", "Entertainment")
    var selectedCategory by remember { mutableStateOf("Dining") }

    // Suggestions for names
    val frequentContacts = listOf("Alice", "Bob", "Charlie", "David", "Eve")

    val currentAmount = amount.toDoubleOrNull() ?: 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_bill), fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                if (participants.isNotEmpty()) {
                    val perPerson = currentAmount / participants.size
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Per Person Estimate:", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$selectedCurrency ${String.format("%.2f", perPerson)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Button(
                    onClick = {
                        val splitData = mutableMapOf<String, Double>()
                        val perPerson = currentAmount / participants.size.coerceAtLeast(1)
                        participants.forEach { p -> splitData[p.id] = perPerson }

                        val bill = DomainBill(
                            id = UUID.randomUUID().toString(),
                            title = title.ifBlank { "Untitled" },
                            category = selectedCategory,
                            amount = currentAmount,
                            currency = selectedCurrency,
                            date = System.currentTimeMillis(),
                            paidBy = selectedPayerId,
                            participants = participants.toList(),
                            splitType = splitType,
                            splitData = splitData,
                            isFavorite = false
                        )
                        viewModel.addBill(bill)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(R.string.save), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title & Amount Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What for?") },
                    placeholder = { Text(stringResource(R.string.title_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.titleLarge
                )
                
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    ExposedDropdownMenuBox(
                        expanded = expandCurrency,
                        onExpandedChange = { expandCurrency = !expandCurrency },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedCurrency,
                            onValueChange = { },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandCurrency) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        )
                        ExposedDropdownMenu(
                            expanded = expandCurrency,
                            onDismissRequest = { expandCurrency = false }
                        ) {
                            allCurrencies.take(150).forEach { curr ->
                                DropdownMenuItem(
                                    text = { Text("${curr.currencyCode} - ${curr.displayName}") },
                                    onClick = {
                                        selectedCurrency = curr.currencyCode
                                        expandCurrency = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = { Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            // Categories
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Category", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = cat == selectedCategory,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // Participants
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Split with (${participants.size})", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                // Add new participant
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newParticipantName,
                        onValueChange = { newParticipantName = it },
                        placeholder = { Text("Name or email") },
                        modifier = Modifier.weight(1f).height(52.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )
                    IconButton(
                        onClick = {
                            if (newParticipantName.isNotBlank()) {
                                participants.add(Participant(id = UUID.randomUUID().toString(), name = newParticipantName.trim()))
                                newParticipantName = ""
                            }
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Person", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                // Suggestions
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                    items(frequentContacts.filter { name -> participants.none { it.name == name } }) { name ->
                        AssistChip(
                            onClick = { participants.add(Participant(id = UUID.randomUUID().toString(), name = name)) },
                            label = { Text("+ $name") },
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Participant List
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    participants.forEach { pt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                                    Text(pt.name.firstOrNull()?.uppercase() ?: "?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                                Text(pt.name, fontWeight = FontWeight.Medium)
                            }
                            if (pt.name != "You") {
                                IconButton(onClick = { 
                                    participants.remove(pt)
                                    if (selectedPayerId == pt.id) selectedPayerId = participants.firstOrNull()?.id ?: ""
                                }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            // Pay & Split settings
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expandPayer,
                    onExpandedChange = { expandPayer = !expandPayer },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = participants.find { it.id == selectedPayerId }?.name ?: "",
                        onValueChange = { },
                        label = { Text("Paid by") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandPayer) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandPayer,
                        onDismissRequest = { expandPayer = false }
                    ) {
                        participants.forEach { pt ->
                            DropdownMenuItem(
                                text = { Text(pt.name) },
                                onClick = {
                                    selectedPayerId = pt.id
                                    expandPayer = false
                                }
                            )
                        }
                    }
                }
                
                ExposedDropdownMenuBox(
                    expanded = expandSplitType,
                    onExpandedChange = { expandSplitType = !expandSplitType },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = splitType.name,
                        onValueChange = { },
                        label = { Text("Split type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandSplitType) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandSplitType,
                        onDismissRequest = { expandSplitType = false }
                    ) {
                        SplitType.values().forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.name) },
                                onClick = {
                                    splitType = st
                                    expandSplitType = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
