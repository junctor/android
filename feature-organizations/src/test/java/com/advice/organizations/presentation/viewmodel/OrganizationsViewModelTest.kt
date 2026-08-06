package com.advice.organizations.presentation.viewmodel

import com.advice.core.local.Organization
import com.advice.organizations.data.repositories.OrganizationsRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class OrganizationsViewModelTest {
    private val vendors = organization(id = 1, tags = listOf(100L))
    private val villages = organization(id = 2, tags = listOf(200L))
    private val both = organization(id = 3, tags = listOf(100L, 200L))

    private val repository =
        mockk<OrganizationsRepository> {
            every { organizations } returns flowOf(listOf(vendors, villages, both))
        }

    @Test
    fun `filters organizations by tag id`() =
        runTest {
            val viewModel = OrganizationsViewModel(repository)

            val result = viewModel.getState(id = 100L).first()

            assertEquals(listOf(vendors, both), result)
        }

    @Test
    fun `unknown tag id returns empty list`() =
        runTest {
            val viewModel = OrganizationsViewModel(repository)

            val result = viewModel.getState(id = 999L).first()

            assertEquals(emptyList<Organization>(), result)
        }

    private fun organization(
        id: Long,
        tags: List<Long>,
    ): Organization =
        Organization(
            id = id,
            name = "Org $id",
            description = null,
            locations = emptyList(),
            links = emptyList(),
            media = emptyList(),
            tag = null,
            tags = tags,
        )
}
