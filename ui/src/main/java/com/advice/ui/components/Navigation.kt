package com.advice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.R

@Composable
internal fun Navigation(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        Icon(
            painterResource(id = R.drawable.arrow_back),
            null,
            modifier = Modifier
                .size(16.dp)
                .rotate(180f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NavigationPreview() {
    ScheduleTheme {
        Navigation("Help & Support") {}
    }
}
