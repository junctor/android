package com.advice.feature_ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.feature_ui.R

@Composable
fun EmptyView() {
    Box(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("404", style = MaterialTheme.typography.displayLarge)
            Text("Maps not found", style = MaterialTheme.typography.displayMedium)
        }
        Icon(
            painterResource(R.drawable.ic_launcher_foreground), null,
            Modifier
                .size(48.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyViewPreview() {
    MaterialTheme {
        EmptyView()
    }
}