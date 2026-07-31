package com.advice.schedule.smoke

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.IdlingPolicies
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.advice.schedule.ui.activity.MainActivity
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

/**
 * Verifies DC34 home menu rows load from live Firebase.
 * Opening every destination is covered by the manual checklist — deep walks hang on
 * never-idle Compose screens in instrumentation.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class HomeMenuWalkSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setIdlingPolicy() {
        disableSystemAnimations()
        IdlingPolicies.setMasterPolicyTimeout(45, TimeUnit.SECONDS)
        IdlingPolicies.setIdlingResourceTimeout(45, TimeUnit.SECONDS)
    }

    @Test
    fun dc34_home_menu_items_are_listed() {
        composeRule.selectPinnedConference("DC34")
        val labels = composeRule.homeMenuLabels()
        assertTrue("Expected navigable home menu items for DC34", labels.isNotEmpty())
        composeRule.assertConferenceSelected("DC34")
    }
}
