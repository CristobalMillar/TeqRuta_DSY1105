package com.example.teqruta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.teqruta.data.local.AppDatabase
import com.example.teqruta.ui.screens.FormScreen
import com.example.teqruta.ui.screens.ListScreen
import com.example.teqruta.ui.viewmodel.ViajeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val database = remember { AppDatabase.getDatabase(context) }

                    val viewModel: ViajeViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ViajeViewModel(database.viajeDao()) as T
                            }
                        }
                    )

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "form") {
                        composable("form") {
                            FormScreen(
                                viewModel = viewModel,
                                onNavigateToList = { navController.navigate("list") }
                            )
                        }
                        composable("list") {
                            ListScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}