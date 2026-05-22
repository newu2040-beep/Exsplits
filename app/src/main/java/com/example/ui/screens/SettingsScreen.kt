package com.example.ui.screens

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.example.R
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Themes", style = MaterialTheme.typography.titleLarge)
            val themes = listOf(
                "Clean Minimalism (Default)", "Amethyst", "Coral", "Sapphire", "Amber",
                "Pastel Pink", "Pastel Blue", "Pastel Green", "Pastel Yellow", "Pastel Purple"
            )
            themes.forEachIndexed { index, name ->
                Button(
                    onClick = { viewModel.setTheme(index) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(name)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Languages (Requires restart/recreate)", style = MaterialTheme.typography.titleLarge)
            
            val languages = listOf("en" to "English", "hi" to "Hindi", "ne" to "Nepali", "ja" to "Japanese", "es" to "Spanish")
            languages.forEach { (code, name) ->
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList.forLanguageTags(code)
                        } else {
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(name)
                }
            }
        }
    }
}
