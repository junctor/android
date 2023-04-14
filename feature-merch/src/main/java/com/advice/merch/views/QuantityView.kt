package com.advice.merch.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.merch.R
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun QuantityView(
    count: Int,
    onRemoveClicked: () -> Unit,
    onAddClicked: () -> Unit,
    canDelete: Boolean,
) {
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onRemoveClicked, Modifier.size(24.dp),
            enabled = canDelete || count > 1
        ) {
            Icon(
                painterResource(if (canDelete && count == 1) R.drawable.ic_delete else R.drawable.ic_remove),
                null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            count.toString(),
            Modifier.defaultMinSize(minWidth = 48.dp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        IconButton(onClick = onAddClicked, Modifier.size(24.dp)) {
            Icon(
                painterResource(R.drawable.ic_add), null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@LightDarkPreview
@Composable
fun QuantityViewPreview() {
    ScheduleTheme {
        QuantityView(1, {}, {}, canDelete = true)
    }
}