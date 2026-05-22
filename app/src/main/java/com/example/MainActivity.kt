package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.BillRepository
import com.example.ui.CreateRoute
import com.example.ui.DetailRoute
import com.example.ui.HomeRoute
import com.example.ui.SettingsRoute
import com.example.ui.screens.BillDetailScreen
import com.example.ui.screens.CreateBillScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ExsplitsTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory

import androidx.navigation.toRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "exsplits-db")
            .fallbackToDestructiveMigration()
            .build()
        val repository = BillRepository(db.billDao())
        val viewModelFactory = MainViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            val currentThemeIndex by viewModel.currentAppTheme.collectAsState()

            ExsplitsTheme(themeIndex = currentThemeIndex) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = HomeRoute) {
                        composable<HomeRoute> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToCreate = { navController.navigate(CreateRoute) },
                                onNavigateToDetail = { id -> navController.navigate(DetailRoute(id)) },
                                onNavigateToSettings = { navController.navigate(SettingsRoute) }
                            )
                        }
                        composable<CreateRoute> {
                            CreateBillScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable<DetailRoute> { backStackEntry ->
                            val route = backStackEntry.toRoute<DetailRoute>()
                            BillDetailScreen(
                                id = route.id,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable<SettingsRoute> {
                            SettingsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
