package com.advice.feedback.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.advice.feedback.models.FeedbackForm
import com.advice.feedback.models.FeedbackType
import com.advice.feedback.ui.components.DisplayOnlyItem
import com.advice.feedback.ui.components.MultiSelectItem
import com.advice.feedback.ui.components.SelectOneItem
import com.advice.feedback.ui.components.TextBoxItem
import com.advice.feedback.ui.preview.FeedbackFormProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun FeedbackScreen(navController: NavController) {
    TODO()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(form: FeedbackForm) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text("{Content} Feedback")
            },
            navigationIcon = {
                IconButton(
                    onClick = { },
                    colors =
                        IconButtonDefaults.iconButtonColors(),
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        "Back",
                        tint = Color.Black,
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { },
                    colors =
                        IconButtonDefaults.iconButtonColors(),
                ) {
                    Icon(
                        Icons.Default.Close,
                        "Close",
                        tint = Color.Black,
                    )
                }
            },
            colors =
                TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
        )
    }) {
        Column(
            modifier =
                Modifier
                    .padding(it)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            form.items.forEach { item ->
                when (val type = item.type) {
                    FeedbackType.DisplayOnly -> DisplayOnlyItem(item.caption)
                    is FeedbackType.SelectOne -> SelectOneItem(item.caption, type.options)
                    is FeedbackType.MultiSelect -> MultiSelectItem(item.caption, type.options)
                    is FeedbackType.TextBox -> TextBoxItem(item.caption, type.value)
                }
            }

            Button({}, modifier = Modifier.fillMaxWidth()) {
                Text("Submit")
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun FeedbackScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackScreen(feedback)
    }
}
