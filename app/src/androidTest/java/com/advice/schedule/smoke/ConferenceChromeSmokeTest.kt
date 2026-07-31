package com.advice.schedule.smoke

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.test.espresso.IdlingPolicies
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.advice.schedule.ui.activity.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@LargeTest
@RunWith(AndroidJUnit4::class)
class ConferenceChromeSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setIdlingPolicy() {
        disableSystemAnimations()
        IdlingPolicies.setMasterPolicyTimeout(45, TimeUnit.SECONDS)
        IdlingPolicies.setIdlingResourceTimeout(45, TimeUnit.SECONDS)
    }

    @Test
    fun dc34_chrome_is_reachable() {
        composeRule.selectPinnedConference("DC34")
        composeRule.assertConferenceSelected("DC34")

        listOf("Schedule", "Maps", "Search", "Settings").forEach { description ->
            composeRule
                .onAllNodesWithContentDescription(description, useUnmergedTree = true)
                .onFirst()
                .assertIsDisplayed()
        }
    }
}
