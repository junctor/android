package com.advice.schedule.smoke

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.IdlingPolicies
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.advice.products.utils.parseCompactOrderData
import com.advice.schedule.ui.activity.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@LargeTest
@RunWith(AndroidJUnit4::class)
class MerchQrSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        disableSystemAnimations()
        IdlingPolicies.setMasterPolicyTimeout(45, TimeUnit.SECONDS)
        IdlingPolicies.setIdlingResourceTimeout(45, TimeUnit.SECONDS)
    }

    @Test
    fun cart_qr_is_valid_compact_order_and_clears_when_empty() {
        val conferences = listOf("DC33", "DC34", "TEST")
        val attempts = mutableListOf<String>()
        var sawMerchMenu = false
        var sawProducts = false
        var exercised = false

        for (conference in conferences) {
            val result =
                runCatching {
                    composeRule.returnToHomePanel()
                    composeRule.selectPinnedConference(conference)
                    val labels = composeRule.homeMenuLabels()
                    val merchLabel =
                        labels.firstOrNull { label ->
                            label.contains("merch", ignoreCase = true) ||
                                label.contains("product", ignoreCase = true) ||
                                label.contains("shop", ignoreCase = true) ||
                                label.contains("store", ignoreCase = true) ||
                                label.contains("swag", ignoreCase = true)
                        }
                    if (merchLabel == null) {
                        return@runCatching "no merch menu label in $labels"
                    }
                    sawMerchMenu = true

                    composeRule.clickHomeMenuLabel(merchLabel)
                    composeRule.safeWaitForIdle()

                    val productCount =
                        runCatching {
                            composeRule.waitUntil(SHORT_TIMEOUT_MS) { productNodeCount() > 0 }
                            productNodeCount()
                        }.getOrDefault(0)

                    if (productCount == 0) {
                        composeRule.pressSystemBack()
                        return@runCatching "merch menu opened but no products found"
                    }
                    sawProducts = true

                    val maxProducts = minOf(productCount, 5)
                    var lastProductError = "no product accepted add-to-cart"
                    for (index in 0 until maxProducts) {
                        lastProductError =
                            exerciseCartQrAtProductIndex(index) ?: run {
                                exercised = true
                                return@runCatching null
                            }
                    }
                    composeRule.pressSystemBack()
                    lastProductError
                }.fold(
                    onSuccess = { it },
                    onFailure = { error ->
                        runCatching { composeRule.pressSystemBack() }
                        runCatching { composeRule.returnToHomePanel() }
                        error.message ?: error.toString()
                    },
                )

            if (exercised) break
            if (result != null) {
                attempts += "$conference: $result"
            }
        }

        assumeTrue(
            "No merch menu on pinned conferences. attempts=$attempts",
            sawMerchMenu,
        )
        assertTrue(
            "Merch UI was reached but cart/QR path failed. " +
                "Often needs variant selection or enable_merch_cart. attempts=$attempts " +
                "sawProducts=$sawProducts",
            exercised,
        )
    }

    /**
     * @return null on success, otherwise a short failure reason for trying the next product.
     */
    private fun productNodeCount(): Int =
        composeRule
            .onAllNodesWithContentDescription("Product ", substring = true, useUnmergedTree = true)
            .fetchSemanticsNodes()
            .size

    private fun clickProductAt(index: Int): String? {
        val products =
            composeRule.onAllNodesWithContentDescription(
                "Product ",
                substring = true,
                useUnmergedTree = true,
            )
        if (index >= products.fetchSemanticsNodes().size) {
            return "product index $index out of range"
        }
        val node = products[index]
        runCatching { node.performScrollTo() }
        node.performClick()
        return null
    }

    private fun clickAddToCart(): Boolean =
        runCatching {
            composeRule
                .onAllNodesWithText("Add to list", substring = false, useUnmergedTree = true)
                .onFirst()
                .assertIsDisplayed()
                .assertIsEnabled()
            composeRule
                .onAllNodesWithText("Add to list", substring = false, useUnmergedTree = true)
                .onFirst()
                .performClick()
            true
        }.getOrDefault(false)

    private fun clickViewListSummary(): Boolean =
        runCatching {
            composeRule.waitUntil(SHORT_TIMEOUT_MS) {
                composeRule
                    .onAllNodesWithText("View List", substring = true, useUnmergedTree = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }
            composeRule
                .onAllNodesWithText("View List", substring = true, useUnmergedTree = true)
                .onFirst()
                .performClick()
            true
        }.getOrDefault(false)

    private fun qrPresent(): Boolean =
        composeRule
            .onAllNodesWithContentDescription("QR Code:", substring = true, useUnmergedTree = true)
            .fetchSemanticsNodes()
            .isNotEmpty()

    private fun exerciseCartQrAtProductIndex(index: Int): String? {
        clickProductAt(index)?.let { return it }
        composeRule.safeWaitForIdle()

        ensureVariantSelectedIfNeeded()

        val addReady =
            runCatching {
                composeRule.waitUntil(SHORT_TIMEOUT_MS) {
                    composeRule
                        .onAllNodesWithText("Add to list", substring = false, useUnmergedTree = true)
                        .fetchSemanticsNodes()
                        .isNotEmpty()
                }
                true
            }.getOrDefault(false)

        if (!addReady || !clickAddToCart()) {
            composeRule.pressSystemBack()
            composeRule.safeWaitForIdle()
            return "add_to_cart missing/disabled (enable_merch_cart off, OOS, or variant not selected)"
        }
        composeRule.safeWaitForIdle()

        if (!clickViewListSummary()) {
            composeRule.pressSystemBack()
            return "products summary not shown after add"
        }
        composeRule.safeWaitForIdle()

        val qrReady =
            runCatching {
                composeRule.waitUntil(SHORT_TIMEOUT_MS) { qrPresent() }
                true
            }.getOrDefault(false)
        if (!qrReady) {
            composeRule.pressSystemBack()
            return "QR not shown after opening summary"
        }

        // Payload from the QR semantics (same string encoded into the bitmap).
        val payload = readQrContentDescription()
        assertDecodableCompactQr(payload)
        val order = parseCompactOrderData(payload)
        assertTrue("Platform must be Android versionCode", order.platform.matches(Regex("A\\d+")))
        assertEquals("Cart quantity after add", 1, order.totalQuantity)
        assertEquals(1, order.items.size)
        val variantId = order.items.single().variantId
        val conferenceId = order.conferenceId

        // Quantity change must rewrite the QR to match cart content.
        composeRule
            .onAllNodesWithContentDescription("Increase quantity", useUnmergedTree = true)
            .onFirst()
            .performClick()
        composeRule.safeWaitForIdle()
        composeRule.waitUntil(SHORT_TIMEOUT_MS) {
            runCatching {
                parseCompactOrderData(readQrContentDescription()).totalQuantity == 2
            }.getOrDefault(false)
        }
        val afterIncrease = parseCompactOrderData(readQrContentDescription())
        assertDecodableCompactQr(readQrContentDescription())
        assertEquals(conferenceId, afterIncrease.conferenceId)
        assertEquals(listOf(variantId to 2), afterIncrease.items.map { it.variantId to it.quantity })

        // Emptying the cart must remove the QR.
        composeRule
            .onAllNodesWithContentDescription("Remove from cart", useUnmergedTree = true)
            .onFirst()
            .performClick()
        composeRule.safeWaitForIdle()
        composeRule.waitUntil(SHORT_TIMEOUT_MS) { !qrPresent() }
        composeRule.onNodeWithText("Nothing in your list").assertIsDisplayed()
        composeRule
            .onNodeWithText("Add some items to generate a QR code")
            .assertIsDisplayed()

        return null
    }

    private fun readQrContentDescription(): String {
        val qrNode =
            composeRule
                .onAllNodesWithContentDescription("QR Code:", substring = true, useUnmergedTree = true)
                .fetchSemanticsNodes()
                .firstOrNull()
                ?: error("QR node missing")
        val description =
            qrNode.config
                .getOrNull(SemanticsProperties.ContentDescription)
                ?.joinToString(" ")
                .orEmpty()
        assertTrue("QR content description missing payload: $description", description.startsWith("QR Code:"))
        return description.removePrefix("QR Code:").trim()
    }

    private fun ensureVariantSelectedIfNeeded() {
        val addAlreadyEnabled =
            runCatching {
                composeRule
                    .onAllNodesWithText("Add to list", substring = false, useUnmergedTree = true)
                    .onFirst()
                    .assertIsEnabled()
                true
            }.getOrDefault(false)
        if (addAlreadyEnabled) return

        val opened =
            runCatching {
                composeRule
                    .onAllNodesWithText("Variant", substring = false, useUnmergedTree = true)
                    .onFirst()
                    .performClick()
                true
            }.getOrDefault(false)
        if (!opened) return
        composeRule.safeWaitForIdle()

        val radioMatcher =
            SemanticsMatcher("Role is RadioButton") { node ->
                node.config.getOrNull(SemanticsProperties.Role) == Role.RadioButton
            }
        val radios =
            runCatching {
                composeRule.onAllNodes(radioMatcher, useUnmergedTree = true).fetchSemanticsNodes()
            }.getOrDefault(emptyList())

        if (radios.isNotEmpty()) {
            composeRule.onAllNodes(radioMatcher, useUnmergedTree = true).onFirst().performClick()
            composeRule.safeWaitForIdle()
            return
        }

        for (size in listOf("M", "L", "S", "XL", "One Size")) {
            val clicked =
                runCatching {
                    composeRule
                        .onAllNodesWithText(size, substring = false, useUnmergedTree = true)
                        .onFirst()
                        .performClick()
                    true
                }.getOrDefault(false)
            if (clicked) {
                composeRule.safeWaitForIdle()
                return
            }
        }
    }
}
