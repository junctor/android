package com.advice.merch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.StockStatus
import com.advice.core.local.products.ProductSelection
import com.advice.merch.R
import com.advice.merch.presentation.state.ProductsState
import com.advice.merch.ui.preview.ProductsProvider
import com.advice.merch.utils.toCurrency
import com.advice.ui.components.Image
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun EditableProduct(
    product: ProductSelection,
    onQuantityChanged: (Int) -> Unit,
    onOutOfStockClick: (() -> Unit)? = null,
) {
    val height = 128.dp
    val outOfStock = product.variant.stockStatus == StockStatus.OUT_OF_STOCK
    val textDecoration = if (outOfStock) TextDecoration.LineThrough else TextDecoration.None

    Box {
        Row(
            Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .alpha(if (outOfStock) 0.7f else 1.0f)
                .then(
                    if (outOfStock && onOutOfStockClick != null) {
                        Modifier.clickable(onClick = onOutOfStockClick)
                    } else {
                        Modifier
                    },
                ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Image
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .height(height)
                    .aspectRatio(0.909f),
            ) {
                Image(
                    model = product.media.firstOrNull()?.url,
                    contentDescription = product.label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                if (outOfStock) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_out_of_stock),
                        tint = MaterialTheme.colorScheme.error,
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .size(48.dp),
                    )
                }
            }

            Column(
                Modifier
                    .height(height)
                    .padding(bottom = 4.dp),
            ) {
                Row {
                    Column(
                        Modifier
                            .weight(1.0f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = product.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 15.sp,
                            textDecoration = textDecoration,
                        )
                        val variant = product.variant
                        Text(
                            text = variant.label,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            textDecoration = textDecoration,
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        PriceLabel(
                            text = product.cost.toCurrency(showCents = true),
                        )
                        if (product.variant.stockStatus == StockStatus.LOW_STOCK) {
                            Spacer(Modifier.height(4.dp))
                            LowStockLabel()
                        }
                        if (outOfStock) {
                            Spacer(Modifier.height(4.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.cd_out_of_stock),
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
                Spacer(Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!outOfStock) {
                        QuantityAdjuster(
                            quantity = product.quantity,
                            onQuantityChanged = onQuantityChanged,
                            canDelete = false,
                            enabled = true,
                            modifier = Modifier.height(32.dp),
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(
                        icon = painterResource(id = R.drawable.ic_delete),
                        onClick = {
                            onQuantityChanged(0)
                        },
                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                        contentDescription = stringResource(R.string.cd_remove_from_cart),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun EditableProductPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    ScheduleTheme {
        Surface {
            val product = state.products.first()
            Column {
                for (variant in product.variants) {
                    EditableProduct(
                        product =
                            ProductSelection(
                                product = product,
                                variant = variant,
                                quantity = 1,
                            ),
                        onQuantityChanged = {},
                    )
                }
            }
        }
    }
}
