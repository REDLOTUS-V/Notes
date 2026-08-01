package com.event.notes.ui

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "main"
    ){
        composable("main"){
            MainScreen(navController = navController)
        }

        composable(
            route = "new_note/{noteId}",
            arguments = listOf(navArgument("noteId"){type = NavType.IntType})
        ) {
            NewNote( navController = navController)
        }
    }
}