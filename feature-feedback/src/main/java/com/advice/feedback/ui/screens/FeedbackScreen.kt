package com.advice.feedback.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackType
import com.advice.feedback.ui.components.DisplayOnlyItem
import com.advice.feedback.ui.components.MultiSelectItem
import com.advice.feedback.ui.components.SelectOneItem
import com.advice.feedback.ui.components.TextBoxItem
import com.advice.feedback.ui.preview.FeedbackFormProvider
import com.advice.ui.components.BackButton
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

@Composable
fun FeedbackContent(
    form: FeedbackForm,
    onValueChanged: (FeedbackItem, String) -> Unit,
    onSubmitPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        form.items.forEach { item ->
            when (val type = item.type) {
                FeedbackType.DisplayOnly -> DisplayOnlyItem(item.caption)
                is FeedbackType.SelectOne -> SelectOneItem(
                    item.caption,
                    type.options,
                    type.selection
                ) {
                    onValueChanged(item, it.toString())
                }

                is FeedbackType.MultiSelect -> MultiSelectItem(
                    item.caption,
                    type.options
                ) {
                    onValueChanged(item, it.toString())
                }

                is FeedbackType.TextBox -> TextBoxItem(item.caption, type.value) {
                    onValueChanged(item, it)
                }
            }
        }

        val label = if (form.hasUserData) "Submit" else "Please enter feedback"
        Button(
            onClick = onSubmitPressed,
            modifier = Modifier.fillMaxWidth(),
            enabled = form.hasUserData,
            shape = roundedCornerShape,
        ) {
            Text(label)
        }
    }
}

@PreviewLightDark
@Composable
private fun FeedbackScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackContent(
            form = feedback,
            onValueChanged = { _, _ -> },
            onSubmitPressed = {},
        )
    }
}
