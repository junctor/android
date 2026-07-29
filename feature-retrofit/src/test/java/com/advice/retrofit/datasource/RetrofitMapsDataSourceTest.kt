package com.advice.retrofit.datasource

import com.advice.core.local.Conference
import com.advice.core.local.ConferenceMap
import com.advice.core.local.FlowResult
import com.advice.core.local.Maps
import com.advice.data.session.UserSession
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class RetrofitMapsDataSourceTest {
    @Test
    fun `empty maps emits success with empty list without downloading`() =
        runTest {
            val conferenceFlow =
                MutableStateFlow<FlowResult<Conference>>(
                    FlowResult.Success(conference(id = 1, maps = emptyList())),
                )
            val downloads = mutableListOf<String>()
            val mapsScope = childMapsScope()
            val subject =
                createSubject(conferenceFlow, mapsScope = mapsScope) { url, _ ->
                    downloads += url
                }

            val result = subject.get().first()

            assertTrue(result is FlowResult.Success)
            assertTrue((result as FlowResult.Success).value.maps.isEmpty())
            assertTrue(downloads.isEmpty())
            mapsScope.cancel()
        }

    @Test
    fun `switching from empty to mapped conference emits loading then success`() =
        runTest {
            val empty = conference(id = 1, maps = emptyList())
            val mapped =
                conference(
                    id = 2,
                    maps =
                        listOf(
                            ConferenceMap(
                                name = "Floor 1",
                                filename = "f1.pdf",
                                url = "https://example.com/f1.pdf",
                            ),
                        ),
                )
            val conferenceFlow =
                MutableStateFlow<FlowResult<Conference>>(FlowResult.Success(empty))

            val emissions = mutableListOf<FlowResult<Maps>>()
            val mapsScope = childMapsScope()
            val subject =
                createSubject(conferenceFlow, mapsScope = mapsScope) { _, destination ->
                    destination.writeBytes(byteArrayOf(1, 2, 3))
                }

            val collectJob =
                launch {
                    subject.get().collect { emissions += it }
                }
            advanceUntilIdle()

            assertTrue(emissions.isNotEmpty())
            assertTrue((emissions.first() as FlowResult.Success).value.maps.isEmpty())

            conferenceFlow.value = FlowResult.Success(mapped)
            advanceUntilIdle()

            assertTrue(emissions.any { it is FlowResult.Loading })
            val success = emissions.filterIsInstance<FlowResult.Success<Maps>>().last()
            assertEquals(2L, success.value.conference.id)
            assertEquals(1, success.value.maps.size)
            assertEquals("Floor 1", success.value.maps.first().name)
            assertTrue(success.value.maps.first().file.exists())

            collectJob.cancel()
            mapsScope.cancel()
        }

    @Test
    fun `cached files emit success without downloading`() =
        runTest {
            val filesDir = tempDir()
            val conference =
                conference(
                    id = 10,
                    maps =
                        listOf(
                            ConferenceMap(
                                name = "Map",
                                filename = "map.pdf",
                                url = "https://example.com/map.pdf",
                            ),
                        ),
                )
            val cacheFile =
                RetrofitMapsDataSource.cacheFile(
                    File(filesDir, "maps/10").also { it.mkdirs() },
                    "map.pdf",
                )
            cacheFile.writeBytes(byteArrayOf(9, 9, 9))

            val downloads = mutableListOf<String>()
            val conferenceFlow =
                MutableStateFlow<FlowResult<Conference>>(FlowResult.Success(conference))
            val mapsScope = childMapsScope()
            val subject =
                createSubject(conferenceFlow, filesDir, mapsScope) { url, _ ->
                    downloads += url
                }

            val result = subject.get().first() as FlowResult.Success
            assertEquals(1, result.value.maps.size)
            assertTrue(downloads.isEmpty())
            mapsScope.cancel()
        }

    @Test
    fun `failed download is omitted from success list`() =
        runTest {
            val conference =
                conference(
                    id = 3,
                    maps =
                        listOf(
                            ConferenceMap(
                                name = "Bad",
                                filename = "bad.pdf",
                                url = "https://example.com/bad.pdf",
                            ),
                            ConferenceMap(
                                name = "Good",
                                filename = "good.pdf",
                                url = "https://example.com/good.pdf",
                            ),
                        ),
                )
            val conferenceFlow =
                MutableStateFlow<FlowResult<Conference>>(FlowResult.Success(conference))
            val mapsScope = childMapsScope()
            val subject =
                createSubject(conferenceFlow, mapsScope = mapsScope) { url, destination ->
                    if (url.contains("bad")) {
                        throw IOException("boom")
                    }
                    destination.writeBytes(byteArrayOf(1))
                }

            val emissions = mutableListOf<FlowResult<Maps>>()
            val collectJob =
                launch {
                    subject.get().collect { emissions += it }
                }
            advanceUntilIdle()

            val success = emissions.filterIsInstance<FlowResult.Success<Maps>>().last()
            assertEquals(listOf("Good"), success.value.maps.map { it.name })

            collectJob.cancel()
            mapsScope.cancel()
        }

    @Test
    fun `cache files are scoped by conference id`() {
        val root = tempDir()
        val a =
            RetrofitMapsDataSource.cacheFile(
                File(root, "maps/1").also { it.mkdirs() },
                "map.pdf",
            )
        val b =
            RetrofitMapsDataSource.cacheFile(
                File(root, "maps/2").also { it.mkdirs() },
                "map.pdf",
            )
        assertFalse(a.absolutePath == b.absolutePath)
        assertTrue(a.path.contains("${File.separator}maps${File.separator}1"))
        assertTrue(b.path.contains("${File.separator}maps${File.separator}2"))
    }

    @Test
    fun `empty file is not treated as valid cache`() {
        val file = File(tempDir(), "empty.pdf").also { it.writeBytes(byteArrayOf()) }
        assertFalse(RetrofitMapsDataSource.isValidCache(file))
    }

    private fun TestScope.childMapsScope(): CoroutineScope =
        CoroutineScope(coroutineContext + SupervisorJob())

    private fun createSubject(
        conferenceFlow: MutableStateFlow<FlowResult<Conference>>,
        filesDir: File = tempDir(),
        mapsScope: CoroutineScope,
        downloader: MapFileDownloader,
    ): RetrofitMapsDataSource {
        val userSession = mockk<UserSession>()
        every { userSession.getConferenceFlow() } returns conferenceFlow
        return RetrofitMapsDataSource(
            userSession = userSession,
            filesDir = filesDir,
            downloader = downloader,
            sharingScope = mapsScope,
        )
    }

    private fun conference(
        id: Long,
        maps: List<ConferenceMap>,
    ): Conference =
        Conference.Zero.copy(
            id = id,
            name = "Conference $id",
            maps = maps,
        )

    private fun tempDir(): File =
        File.createTempFile("maps-test", "dir").apply {
            delete()
            mkdirs()
            deleteOnExit()
        }
}
