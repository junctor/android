package com.advice.schedule.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.advice.merch.ui.components.QuantityAdjuster
import com.advice.ui.components.BookmarkButton
import com.advice.ui.components.SearchBar
import com.advice.ui.theme.ScheduleTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Narrow semantics guards for shared controls. Does not launch the full app or
 * TalkBack; asserts content descriptions that assistive tech and smoke locators rely on.
 */
@RunWith(AndroidJUnit4::class)
class AccessibilitySemanticsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun bookmarkButton_announcesAddAndRemove() {
        var isBookmarked by mutableStateOf(false)
        composeRule.setContent {
            ScheduleTheme {
                Column {
                    BookmarkButton(isBookmarked = isBookmarked, onCheckChange = {})
                }
            }
        }
        composeRule.onNodeWithContentDescription("Add bookmark").assertIsDisplayed()

        isBookmarked = true
        composeRule.onNodeWithContentDescription("Remove bookmark").assertIsDisplayed()
    }

    @Test
    fun searchBar_clearControlIsLabeled() {
        composeRule.setContent {
            ScheduleTheme {
                SearchBar(query = "defcon", placeholder = "Search", onQuery = {})
            }
        }
        composeRule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
    }

    @Test
    fun quantityAdjuster_announcesIncreaseDecreaseAndRemove() {
        var quantity by mutableStateOf(2)
        composeRule.setContent {
            ScheduleTheme {
                QuantityAdjuster(
                    quantity = quantity,
                    onQuantityChanged = {},
                    canDelete = true,
                )
            }
        }
        composeRule.onNodeWithContentDescription("Increase quantity").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Decrease quantity").assertIsDisplayed()

        quantity = 1
        composeRule.onNodeWithContentDescription("Remove from cart").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Increase quantity").assertIsDisplayed()
    }
}
