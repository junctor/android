package com.advice.products.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun PromoSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .clickable {
                onCheckedChange(!checked)
            }
            .padding(16.dp)
    ) {
        Column(Modifier.weight(1.0f)) {
            Text(title)
            Text(description)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White
            )
        )
    }
}

@LightDarkPreview
@Composable
fun PromoSwitchPreview() {
    ScheduleTheme {
        PromoSwitch(
            title = "Goon Discount",
            description = "Must show Goon Badge",
            checked = true,
            onCheckedChange = {}
        )
    }
}
