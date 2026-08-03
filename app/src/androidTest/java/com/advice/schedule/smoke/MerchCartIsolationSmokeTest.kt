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
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Merch carts are per-conference. Seed a cart on DEF CON 32, switch to DEF CON 33,
 * and assert 33 does not show 32's QR / cart payload.
 *
 * DC32 and DC33 both ship usable merch + cart paths in live Firebase.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class MerchCartIsolationSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        disableSystemAnimations()
        IdlingPolicies.setMasterPolicyTimeout(45, TimeUnit.SECONDS)
        IdlingPolicies.setIdlingResourceTimeout(45, TimeUnit.SECONDS)
    }

    @Test
    fun cart_does_not_leak_from_dc32_to_dc33() {
        // --- Seed cart on DC32 ---
        composeRule.selectPinnedConference("DC32")
        composeRule.assertConferenceSelected("DC32")
        openMerchCatalogOrFail("DC32")
        addFirstProductAndOpenSummaryOrFail("DC32")
        composeRule.waitUntil(SHORT_TIMEOUT_MS) { qrPresent() }
        val sourcePayload = readQrContentDescription()
        val sourceOrder = parseCompactOrderData(sourcePayload)
        assertTrue(
            "DC32 QR platform must be Android versionCode",
            sourceOrder.platform.matches(Regex("A\\d+")),
        )
        assertTrue("DC32 cart QR must contain at least one line", sourceOrder.items.isNotEmpty())
        val sourceConferenceId = sourceOrder.conferenceId

        // Peel merch stack (summary → product/catalog → shell) before switching.
        leaveMerchToHome()

        // --- Switch to DC33 and confirm cart isolation ---
        composeRule.selectPinnedConference("DC33")
        composeRule.assertConferenceSelected("DC33")
        openMerchCatalogOrFail("DC33")

        val hasViewList =
            composeRule
                .onAllNodesWithText("View List", substring = true, useUnmergedTree = true)
                .fetchSemanticsNodes()
                .isNotEmpty()

        if (!hasViewList) {
            // Empty cart on DC33 — isolation holds (DC32's items did not appear).
            return
        }

        assertTrue("DC33 View List should open after cart seed check", clickViewListSummary())
        composeRule.safeWaitForIdle()

        if (!qrPresent()) {
            composeRule.onNodeWithText("Nothing in your list").assertIsDisplayed()
            return
        }

        val otherPayload = readQrContentDescription()
        val otherOrder = parseCompactOrderData(otherPayload)
        assertNotEquals(
            "DC33 cart QR must not reuse DC32 conference id ($sourceConferenceId). " +
                "source=$sourcePayload other=$otherPayload",
            sourceConferenceId,
            otherOrder.conferenceId,
        )
    }

    private fun leaveMerchToHome() {
        composeRule.dismissBlockingDialogs()
        // Prefer in-app Back over repeated system backs — excess system backs can finish MainActivity.
        repeat(8) {
            val onHome =
                composeRule
                    .onAllNodesWithContentDescription("Conference selector", useUnmergedTree = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            if (onHome) {
                composeRule.returnToHomePanel()
                return
            }
            val clickedBack =
                runCatching {
                    composeRule
                        .onAllNodesWithContentDescription("Back", useUnmergedTree = true)
                        .onFirst()
                        .performClick()
                    true
                }.getOrDefault(false)
            if (!clickedBack) {
                composeRule.pressSystemBack()
            }
            composeRule.safeWaitForIdle()
            composeRule.dismissBlockingDialogs()
        }
        composeRule.returnToHomePanel()
    }

    private fun openMerchCatalogOrFail(conference: String) {
        val labels = composeRule.homeMenuLabels()
        val merchLabel =
            labels.firstOrNull { label ->
                label.contains("merch", ignoreCase = true) ||
                    label.contains("product", ignoreCase = true) ||
                    label.contains("shop", ignoreCase = true) ||
                    label.contains("store", ignoreCase = true) ||
                    label.contains("swag", ignoreCase = true)
            }
        assertTrue(
            "$conference: expected a merch/products home-menu row. labels=$labels",
            merchLabel != null,
        )
        composeRule.clickHomeMenuLabel(merchLabel!!)
        composeRule.safeWaitForIdle()
        val productsReady =
            runCatching {
                composeRule.waitUntil(SHORT_TIMEOUT_MS) { productNodeCount() > 0 }
                true
            }.getOrDefault(false)
        assertTrue(
            "$conference: merch menu opened but no product tiles loaded",
            productsReady && productNodeCount() > 0,
        )
    }

    private fun addFirstProductAndOpenSummaryOrFail(conference: String) {
        val count = productNodeCount()
        var lastError = "no product accepted add-to-cart"
        for (index in 0 until minOf(count, 5)) {
            lastError = tryAddAt(index) ?: return
        }
        fail("$conference: could not add an in-stock product to the cart. last=$lastError")
    }

    /** @return null on success, otherwise a short failure reason. */
    private fun tryAddAt(index: Int): String? {
        clickProductAt(index)
        composeRule.safeWaitForIdle()
        ensureVariantSelectedIfNeeded()
        if (!clickAddToCart()) {
            composeRule.pressSystemBack()
            composeRule.safeWaitForIdle()
            return "add_to_cart missing/disabled at product index $index"
        }
        composeRule.safeWaitForIdle()
        if (!clickViewListSummary()) {
            composeRule.pressSystemBack()
            return "View List not shown after add at product index $index"
        }
        composeRule.safeWaitForIdle()
        return null
    }

    private fun productNodeCount(): Int =
        composeRule
            .onAllNodesWithContentDescription("Product ", substring = true, useUnmergedTree = true)
            .fetchSemanticsNodes()
            .size

    private fun clickProductAt(index: Int) {
        val products =
            composeRule.onAllNodesWithContentDescription(
                "Product ",
                substring = true,
                useUnmergedTree = true,
            )
        val node = products[index]
        runCatching { node.performScrollTo() }
        node.performClick()
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
