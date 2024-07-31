package com.encora.topsongsapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.encora.topsongsapp.presentation.screen.SongDetailScreen
import com.encora.topsongsapp.presentation.screen.SongListScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "songList") {
        composable("songList") {
            SongListScreen(navController = navController)
        }
        composable("songDetail/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?:"0"
            SongDetailScreen(songId = songId,navController)
        }
    }
}