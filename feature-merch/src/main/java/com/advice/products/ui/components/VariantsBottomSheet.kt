package com.advice.products.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.products.ProductVariant
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VariantsBottomSheet(
    variants: List<ProductVariant>,
    selection: ProductVariant?,
    onVariantSelected: (ProductVariant) -> Unit,
    onDismiss: () -> Unit,
    canAdd: Boolean
) {
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        tonalElevation = 0.dp,
    ) {
        Column {
            for (variant in variants) {
                VariantRow(
                    variant = variant,
                    isSelected = selection == variant,
                    onSelect = { onVariantSelected(variant) },
                    canAdd = canAdd,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun VariantsBottomSheetPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    ScheduleTheme {
        VariantsBottomSheet(
            variants = state.products.first().variants,
            selection = null,
            onVariantSelected = {},
            onDismiss = {},
            canAdd = true,
        )
    }
}
