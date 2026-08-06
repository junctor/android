package com.advice.documents.presentation.viewmodel

import com.advice.core.local.Document
import com.advice.documents.data.repositories.DocumentsRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DocumentsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<DocumentsRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `null id emits error state`() =
        runTest {
            val viewModel = DocumentsViewModel(repository)

            viewModel.get(id = null)
            advanceUntilIdle()

            assertTrue(viewModel.state.first() is DocumentsScreenState.Error)
        }

    @Test
    fun `missing document emits error state`() =
        runTest {
            coEvery { repository.get(1L) } returns null
            val viewModel = DocumentsViewModel(repository)

            viewModel.get(id = 1L)
            advanceUntilIdle()

            assertTrue(viewModel.state.first() is DocumentsScreenState.Error)
        }

    @Test
    fun `found document emits success state`() =
        runTest {
            val document = Document(1, "Code of Conduct", "Be excellent")
            coEvery { repository.get(1L) } returns document
            val viewModel = DocumentsViewModel(repository)

            viewModel.get(id = 1L)
            advanceUntilIdle()

            val state = viewModel.state.first()
            assertTrue(state is DocumentsScreenState.Success)
            assertEquals(document, (state as DocumentsScreenState.Success).document)
        }
}
