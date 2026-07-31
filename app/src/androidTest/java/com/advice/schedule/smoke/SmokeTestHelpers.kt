package com.advice.schedule.smoke

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.advice.schedule.ui.activity.MainActivity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

internal const val SMOKE_TIMEOUT_MS = 60_000L
internal const val SHORT_TIMEOUT_MS = 20_000L
internal const val RECOVERY_TIMEOUT_MS = 12_000L

private const val HOME_MENU_PREFIX = "Home menu: "

private typealias SmokeComposeRule =
    AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

private fun SmokeComposeRule.hasContentDesc(desc: String): Boolean =
    runCatching {
        onAllNodesWithContentDescription(desc, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
    }.getOrDefault(false)

private fun SmokeComposeRule.hasConferenceSelector(): Boolean = hasContentDesc("Conference selector")

private fun SmokeComposeRule.hasScheduleMenu(): Boolean = hasContentDesc("Menu")

private fun SmokeComposeRule.hasScheduleNav(): Boolean = hasContentDesc("Schedule")

private fun SmokeComposeRule.hasHomeMenuItems(): Boolean =
    runCatching {
        onAllNodesWithContentDescription(HOME_MENU_PREFIX, substring = true, useUnmergedTree = true)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }.getOrDefault(false)

private fun SmokeComposeRule.clickConferenceSelector() {
    check(hasContentDesc("Conference selector")) { "Conference selector not on screen" }
    onAllNodesWithContentDescription("Conference selector", useUnmergedTree = true)
        .onFirst()
        .performClick()
}

private fun SmokeComposeRule.clickScheduleMenu() {
    check(hasContentDesc("Menu")) { "Schedule menu not on screen" }
    onAllNodesWithContentDescription("Menu", useUnmergedTree = true).onFirst().performClick()
}

private fun SmokeComposeRule.clickScheduleNav() {
    check(hasContentDesc("Schedule")) { "Schedule nav not on screen" }
    onAllNodesWithContentDescription("Schedule", useUnmergedTree = true).onFirst().performClick()
}

internal fun SmokeComposeRule.dismissBlockingDialogs() {
    listOf("Dismiss", "Close", "Cancel", "Not now", "No thanks", "Skip").forEach { label ->
        runCatching {
            if (hasContentDesc(label)) {
                onAllNodesWithContentDescription(label, useUnmergedTree = true).onFirst().performClick()
                safeWaitForIdle()
            }
        }
        runCatching {
            if (onAllNodesWithText(label, substring = false, useUnmergedTree = true)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            ) {
                onAllNodesWithText(label, substring = false, useUnmergedTree = true).onFirst().performClick()
                safeWaitForIdle()
            }
        }
    }
}

internal fun SmokeComposeRule.openHomePanel() {
    dismissBlockingDialogs()
    // Nested destinations (Maps/Search/Settings) hide home chrome; peel them first.
    for (i in 0 until 6) {
        if (hasConferenceSelector() || hasScheduleMenu() || hasScheduleNav()) {
            break
        }
        pressSystemBack()
    }
    waitUntil(SMOKE_TIMEOUT_MS) {
        dismissBlockingDialogs()
        hasConferenceSelector() || hasScheduleMenu() || hasScheduleNav()
    }
    if (!hasConferenceSelector()) {
        when {
            hasScheduleMenu() -> clickScheduleMenu()
            hasScheduleNav() -> {
                clickScheduleNav()
                safeWaitForIdle()
                if (hasScheduleMenu()) clickScheduleMenu()
            }
        }
        safeWaitForIdle()
        waitUntil(SMOKE_TIMEOUT_MS) {
            dismissBlockingDialogs()
            hasConferenceSelector()
        }
    }
    // Wait until Firebase-backed home is loaded (title is not the placeholder "Home").
    waitUntil(SMOKE_TIMEOUT_MS) {
        dismissBlockingDialogs()
        runCatching {
            val title = readConferenceTitle()
            !title.equals("Home", ignoreCase = true)
        }.getOrDefault(false) ||
            hasHomeMenuItems()
    }
}

internal fun matchesPinnedConference(
    haystack: String,
    target: String,
): Boolean {
    val text = haystack.uppercase()
    return when (target.uppercase()) {
        "DC34" ->
            text.contains("DC34") ||
                text.contains("DEFCON34") ||
                text.contains("DEF CON 34") ||
                text.contains("DEFCON 34")

        "DC33" ->
            text.contains("DC33") ||
                text.contains("DEFCON33") ||
                text.contains("DEF CON 33") ||
                text.contains("DEFCON 33")

        "TEST" ->
            (
                text.contains("TEST") ||
                    text.contains("TESTCON") ||
                    text.contains("TEST CON")
            ) &&
                !text.contains("DC34") &&
                !text.contains("DC33") &&
                !text.contains("DEFCON34") &&
                !text.contains("DEFCON33") &&
                !text.contains("DEF CON 34") &&
                !text.contains("DEF CON 33")

        else -> text.contains(target.uppercase())
    }
}

private fun collectTexts(node: androidx.compose.ui.semantics.SemanticsNode): List<String> {
    val own =
        node.config
            .getOrNull(SemanticsProperties.Text)
            ?.map { it.text }
            .orEmpty()
    val children = node.children.flatMap { collectTexts(it) }
    return own + children
}

internal fun SmokeComposeRule.readConferenceTitle(): String {
    val node =
        onAllNodesWithContentDescription("Conference selector", useUnmergedTree = true)
            .fetchSemanticsNodes()
            .firstOrNull()
            ?: error("Conference selector not on screen")
    return collectTexts(node).firstOrNull { it.isNotBlank() && !it.equals("Home", ignoreCase = true) }
        ?: collectTexts(node).firstOrNull { it.isNotBlank() }
        ?: error("Conference selector has no title text")
}

internal fun SmokeComposeRule.currentConferenceTitle(): String {
    openHomePanel()
    return readConferenceTitle()
}

internal fun SmokeComposeRule.selectPinnedConference(target: String) {
    openHomePanel()
    val before = runCatching { readConferenceTitle() }.getOrNull()
    if (before != null && matchesPinnedConference(before, target)) {
        return
    }

    clickConferenceSelector()
    safeWaitForIdle()

    waitUntil(SMOKE_TIMEOUT_MS) {
        onAllNodesWithContentDescription("Conference ", substring = true, useUnmergedTree = true)
            .fetchSemanticsNodes()
            .isNotEmpty()
    }

    val rows =
        onAllNodesWithContentDescription("Conference ", substring = true, useUnmergedTree = true)
            .fetchSemanticsNodes()

    val match =
        rows.firstOrNull { node ->
            val desc =
                node.config
                    .getOrNull(SemanticsProperties.ContentDescription)
                    ?.joinToString(" ")
                    .orEmpty()
            val texts = collectTexts(node)
            matchesPinnedConference(desc.removePrefix("Conference "), target) ||
                texts.any { matchesPinnedConference(it, target) }
        } ?: error(
            "Pinned conference $target not found. Rows=" +
                rows.joinToString { node ->
                    val desc =
                        node.config
                            .getOrNull(SemanticsProperties.ContentDescription)
                            ?.joinToString(" ")
                            .orEmpty()
                    "$desc:${collectTexts(node)}"
                },
        )

    val titleHint = collectTexts(match).firstOrNull { it.isNotBlank() }
    val descHint =
        match.config
            .getOrNull(SemanticsProperties.ContentDescription)
            ?.joinToString(" ")

    when {
        !descHint.isNullOrBlank() -> {
            val row =
                onAllNodesWithContentDescription(descHint, substring = false, useUnmergedTree = true)
                    .onFirst()
            runCatching { row.performScrollTo() }
            row.performClick()
        }
        titleHint != null -> {
            onAllNodesWithText(titleHint, substring = false, useUnmergedTree = true)
                .onFirst()
                .performClick()
        }
        else -> error("Matched conference row has no clickable locator")
    }
    safeWaitForIdle()

    // If still on the previous conference, try clicking by exact title text inside the dropdown.
    openHomePanel()
    if (!matchesPinnedConference(runCatching { readConferenceTitle() }.getOrNull().orEmpty(), target) &&
        titleHint != null
    ) {
        clickConferenceSelector()
        safeWaitForIdle()
        runCatching {
            onAllNodesWithText(titleHint, substring = false, useUnmergedTree = true)
                .onFirst()
                .performScrollTo()
            onAllNodesWithText(titleHint, substring = false, useUnmergedTree = true)
                .onFirst()
                .performClick()
        }
        safeWaitForIdle()
    }

    val deadline = System.currentTimeMillis() + SMOKE_TIMEOUT_MS
    var lastTitle: String? = before
    while (System.currentTimeMillis() < deadline) {
        openHomePanel()
        lastTitle = runCatching { readConferenceTitle() }.getOrNull()
        if (lastTitle != null && matchesPinnedConference(lastTitle, target)) {
            return
        }
        Thread.sleep(750)
    }
    error("Timed out selecting $target; title='$lastTitle'; hint=$titleHint")
}

/**
 * Prefer a short settle delay over [waitForIdle]. Live screens (maps, loaders, infinite
 * animations) can keep Compose never-idle and hang instrumentation for minutes.
 */
internal fun SmokeComposeRule.safeWaitForIdle() {
    Thread.sleep(450)
}

/** Disable system animations so Compose idle checks can complete. */
internal fun disableSystemAnimations() {
    val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
    listOf(
        "settings put global window_animation_scale 0",
        "settings put global transition_animation_scale 0",
        "settings put global animator_duration_scale 0",
    ).forEach { cmd ->
        runCatching {
            automation.executeShellCommand(cmd).use { pfd ->
                java.io.FileInputStream(pfd.fileDescriptor).use { input ->
                    input.copyTo(java.io.OutputStream.nullOutputStream())
                }
            }
        }
    }
}

internal fun SmokeComposeRule.pressSystemBack() {
    runCatching {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack()
    }.onFailure {
        runCatching {
            runOnUiThread {
                activity.onBackPressedDispatcher.onBackPressed()
            }
        }
    }
    safeWaitForIdle()
}

internal fun SmokeComposeRule.assertNoLoadFailure() {
    runCatching {
        val couldNotLoad =
            onAllNodes(hasText("Could not load", substring = true), useUnmergedTree = true)
                .fetchSemanticsNodes()
        assertTrue("Unexpected load failure UI", couldNotLoad.isEmpty())
    }
}

internal fun SmokeComposeRule.homeMenuLabels(): List<String> {
    openHomePanel()
    waitUntil(SMOKE_TIMEOUT_MS) { hasHomeMenuItems() }
    return onAllNodesWithContentDescription(HOME_MENU_PREFIX, substring = true, useUnmergedTree = true)
        .fetchSemanticsNodes()
        .mapNotNull { node ->
            node.config
                .getOrNull(SemanticsProperties.ContentDescription)
                ?.joinToString(" ")
                ?.removePrefix(HOME_MENU_PREFIX)
                ?.takeIf { it.isNotBlank() }
        }.distinct()
}

internal fun SmokeComposeRule.clickHomeMenuLabel(label: String) {
    openHomePanel()
    val menuDesc = "$HOME_MENU_PREFIX$label"
    waitUntil(SHORT_TIMEOUT_MS) {
        onAllNodesWithContentDescription(menuDesc, substring = false, useUnmergedTree = true)
            .fetchSemanticsNodes()
            .isNotEmpty() ||
            onAllNodesWithText(label, substring = true, useUnmergedTree = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
    }
    val byDesc =
        onAllNodesWithContentDescription(menuDesc, substring = false, useUnmergedTree = true)
            .fetchSemanticsNodes()
    val interaction =
        if (byDesc.isNotEmpty()) {
            onAllNodesWithContentDescription(menuDesc, substring = false, useUnmergedTree = true).onFirst()
        } else {
            onAllNodesWithText(label, substring = true, useUnmergedTree = true).onFirst()
        }
    runCatching { interaction.performScrollTo() }
    interaction.performClick()
    safeWaitForIdle()
}

internal fun SmokeComposeRule.navigateChromeByDescription(contentDescription: String) {
    openHomePanel()
    waitUntil(SHORT_TIMEOUT_MS) { hasContentDesc(contentDescription) }
    onAllNodesWithContentDescription(contentDescription, useUnmergedTree = true)
        .onFirst()
        .assertIsDisplayed()
        .performClick()
    safeWaitForIdle()
}

internal fun SmokeComposeRule.returnToHomePanel() {
    repeat(8) {
        dismissBlockingDialogs()
        when {
            hasConferenceSelector() -> return
            hasScheduleMenu() -> {
                runCatching { clickScheduleMenu() }
                safeWaitForIdle()
                if (hasConferenceSelector()) return
            }
        }
        pressSystemBack()
    }
    runCatching {
        waitUntil(RECOVERY_TIMEOUT_MS) {
            hasConferenceSelector() || hasScheduleMenu() || hasScheduleNav()
        }
        if (!hasConferenceSelector()) {
            when {
                hasScheduleMenu() -> clickScheduleMenu()
                hasScheduleNav() -> {
                    clickScheduleNav()
                    safeWaitForIdle()
                    if (hasScheduleMenu()) clickScheduleMenu()
                }
            }
            safeWaitForIdle()
        }
    }
}

internal fun SmokeComposeRule.assertConferenceSelected(target: String) {
    val title = currentConferenceTitle()
    assertTrue(
        "Expected conference matching $target but was '$title'",
        matchesPinnedConference(title, target),
    )
}

internal fun SmokeComposeRule.assertTitleChanged(
    previous: String,
    target: String,
) {
    val title = currentConferenceTitle()
    assertFalse("Conference title did not change from '$previous'", title == previous)
    assertTrue(
        "Expected conference matching $target but was '$title'",
        matchesPinnedConference(title, target),
    )
}
