package com.advice.schedule.smoke

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.advice.schedule.ui.activity.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ConferenceSwitchSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun disableAnims() {
        disableSystemAnimations()
    }

    @Test
    fun switch_dc34_to_dc33_to_test_updates_home_title() {
        composeRule.selectPinnedConference("DC34")
        composeRule.assertConferenceSelected("DC34")
        val dc34Title = composeRule.currentConferenceTitle()

        composeRule.selectPinnedConference("DC33")
        composeRule.assertTitleChanged(dc34Title, "DC33")
        val dc33Title = composeRule.currentConferenceTitle()
        composeRule.homeMenuLabels()

        composeRule.selectPinnedConference("TEST")
        composeRule.assertTitleChanged(dc33Title, "TEST")
        composeRule.homeMenuLabels()
    }
}
