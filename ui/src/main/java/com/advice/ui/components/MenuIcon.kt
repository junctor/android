package com.advice.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.advice.ui.R
import com.advice.ui.utils.toResource
import timber.log.Timber


@Composable
fun MenuIcon(resource: String?) {
    val drawable = resource?.toResource()
    if (drawable != null) {
        Icon(
            painterResource(id = drawable),
            contentDescription = resource,
        )
    } else {
        Timber.e("Unknown icon: $resource")
        Icon(
            painterResource(id = R.drawable.baseline_broken_24),
            contentDescription = resource,
        )
    }
}
