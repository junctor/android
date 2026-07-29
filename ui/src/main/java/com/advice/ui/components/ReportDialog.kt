package com.advice.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.advice.ui.R
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun ReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var message by remember { mutableStateOf("") }
    val canSubmit = message.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.report_dialog_title)) },
        text = {
            TextField(
                value = message,
                onValueChange = { message = it },
                placeholder = {
                    Text(stringResource(R.string.report_message_hint))
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 8,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmit(message.trim()) },
                enabled = canSubmit,
            ) {
                Text(stringResource(R.string.report_submit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.report_cancel))
            }
        },
        tonalElevation = 0.dp,
    )
}

@PreviewLightDark
@Composable
private fun ReportDialogPreview() {
    ScheduleTheme {
        ReportDialog(
            onDismiss = {},
            onSubmit = {},
        )
    }
}
