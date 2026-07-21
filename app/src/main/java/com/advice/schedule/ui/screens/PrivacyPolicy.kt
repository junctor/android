package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.advice.schedule.navigation.onBackPressed
import com.advice.ui.screens.PrivacyPolicyScreen

@Composable
fun PrivacyPolicy(navController: NavHostController) {
    PrivacyPolicyScreen(
        onBackPress = { navController.onBackPressed() },
    )
}
