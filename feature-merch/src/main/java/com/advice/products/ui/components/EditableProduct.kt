package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductSelection
import com.advice.products.R
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.preview.ProductsProvider
import com.advice.products.utils.toCurrency
import com.advice.ui.components.Image
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

@Composable
internal fun EditableProduct(
    product: ProductSelection,
    onQuantityChanged: (Int) -> Unit,
) {
    val height = 128.dp

    Box {
        val inStock = product.variant.stockStatus != StockStatus.OUT_OF_STOCK
        Row(
            Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            //  .alpha(if (inStock) 1.0f else 0.5f)
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Image
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .height(height)
                    .aspectRatio(0.909f)
            ) {
                Image(
                    model = product.media.firstOrNull()?.url,
                    contentDescription = product.label,
                    contentScale = ContentScale.Crop,
                )
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
                        // label
                        Text(
                            text = product.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 15.sp,
                        )
                        val variant = product.variant
                        if (variant != null) {
                            Text(
                                text = variant.label,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Column(
                        horizontalAlignment = Alignment.End,
                    ) {
                        PriceLabel(
                            text = product.cost.toCurrency(showCents = true),
                        )
                        // Stock status
                        if (product.variant?.stockStatus == StockStatus.LOW_STOCK) {
                            Spacer(Modifier.height(4.dp))
                            LowStockLabel()
                        }
                    }
                }
                Spacer(Modifier.weight(1f))

                if (product.variant?.stockStatus == StockStatus.OUT_OF_STOCK) {
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.errorContainer,
                                shape = roundedCornerShape
                            )
                            .height(32.dp)
                            .padding(horizontal = 8.dp, vertical = 0.dp)
                            .align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Out of Stock",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Delete",
                            tint = iconButtonForegroundColor,
                            modifier = Modifier
                                .size(14.dp),
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        QuantityAdjuster(
                            quantity = product.quantity,
                            onQuantityChanged = onQuantityChanged,
                            canDelete = false,
                            enabled = inStock,
                            modifier = Modifier.height(32.dp),
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        IconButton(
                            icon = painterResource(id = R.drawable.ic_delete),
                            onClick = {
                                onQuantityChanged(0)
                            },
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun EditableProductPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        Surface {
            val product = state.products.first()
            Column {
                for (variant in product.variants) {
                    EditableProduct(
                        product = ProductSelection(
                            product = product,
                            variant = variant,
                            quantity = 1
                        ),
                        onQuantityChanged = {},
                    )
                }
            }
        }
    }
}
