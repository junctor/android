package com.advice.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun ReportSection(
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ReportDialog(
            onDismiss = { showDialog = false },
            onSubmit = { message ->
                showDialog = false
                onSubmit(message)
            },
        )
    }

    ReportLink(
        onClick = { showDialog = true },
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun ReportSectionPreview() {
    ScheduleTheme {
        ReportSection(onSubmit = {})
    }
}
