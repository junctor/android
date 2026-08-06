package com.advice.menu.data.repositories

import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.GENERAL_FEEDBACK_TITLE
import com.advice.data.sources.FeedbackDataSource
import com.advice.data.sources.MenuDataSource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Glue test for the combine of menus and feedback forms. The gating algorithm
 * itself ([com.advice.core.local.withFeedbackGating]) is covered in :core.
 */
class MenuRepositoryTest {
    private val menuWithFeedback =
        Menu(
            id = 1,
            label = "Home",
            items =
                listOf(
                    MenuItem.Navigation("feedback", "Feedback", null, "feedback"),
                    MenuItem.Navigation("news", "News", null, "news"),
                ),
        )

    private val generalForm =
        FeedbackForm(
            id = 10,
            conference = 1,
            title = GENERAL_FEEDBACK_TITLE,
            items = emptyList(),
            endpoint = "https://example.com/feedback",
        )

    @Test
    fun `success applies feedback gating to menus`() =
        runTest {
            val subject = createRepository(FlowResult.Success(listOf(menuWithFeedback)), emptyList())

            val result = subject.get().first()

            assertTrue(result is FlowResult.Success)
            val items = (result as FlowResult.Success).value.single().items
            // No general feedback form available, so the feedback item is gated out.
            assertEquals(listOf("News"), items.map { it.label })
        }

    @Test
    fun `success keeps feedback item when general form exists`() =
        runTest {
            val subject =
                createRepository(FlowResult.Success(listOf(menuWithFeedback)), listOf(generalForm))

            val result = subject.get().first()

            assertTrue(result is FlowResult.Success)
            val items = (result as FlowResult.Success).value.single().items
            assertEquals(2, items.size)
        }

    @Test
    fun `loading and failure pass through unchanged`() =
        runTest {
            val loading = createRepository(FlowResult.Loading, emptyList()).get().first()
            assertTrue(loading is FlowResult.Loading)

            val failure =
                createRepository(FlowResult.Failure(IllegalStateException("boom")), emptyList())
                    .get()
                    .first()
            assertTrue(failure is FlowResult.Failure)
        }

    private fun createRepository(
        menus: FlowResult<List<Menu>>,
        forms: List<FeedbackForm>,
    ): MenuRepository {
        val menuDataSource =
            object : MenuDataSource {
                override fun get() = flowOf(menus)
            }
        val feedbackDataSource =
            object : FeedbackDataSource {
                override fun get() = flowOf(forms)
            }
        return MenuRepository(menuDataSource, feedbackDataSource)
    }
}
