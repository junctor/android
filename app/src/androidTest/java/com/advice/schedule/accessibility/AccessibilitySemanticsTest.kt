package com.advice.schedule.accessibility

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.advice.products.ui.components.QuantityAdjuster
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
        composeRule.setContent {
            ScheduleTheme {
                Column {
                    BookmarkButton(isBookmarked = false, onCheckChange = {})
                }
            }
        }
        composeRule.onNodeWithContentDescription("Add bookmark").assertIsDisplayed()

        composeRule.setContent {
            ScheduleTheme {
                BookmarkButton(isBookmarked = true, onCheckChange = {})
            }
        }
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
        composeRule.setContent {
            ScheduleTheme {
                QuantityAdjuster(
                    quantity = 2,
                    onQuantityChanged = {},
                    canDelete = true,
                )
            }
        }
        composeRule.onNodeWithContentDescription("Increase quantity").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Decrease quantity").assertIsDisplayed()

        composeRule.setContent {
            ScheduleTheme {
                QuantityAdjuster(
                    quantity = 1,
                    onQuantityChanged = {},
                    canDelete = true,
                )
            }
        }
        composeRule.onNodeWithContentDescription("Remove from cart").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Increase quantity").assertIsDisplayed()
    }
}
