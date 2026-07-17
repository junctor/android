package com.advice.schedule.di

import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.data.session.UserSession
import com.advice.data.sources.MapsDataSource
import com.advice.retrofit.datasource.RetrofitMapsDataSource
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

class ApplicationCoroutineScopeLifecycleTest {

    @Test
    fun `application scope is cancelled when module is unloaded`() {
        val lifecycleModule =
            module {
                single(named(APPLICATION_SCOPE)) {
                    CoroutineScope(SupervisorJob() + Dispatchers.IO)
                } withOptions {
                    onClose { it?.cancel() }
                }
            }

        val koin =
            startKoin {
                modules(lifecycleModule)
            }.koin

        try {
            val scope = koin.get<CoroutineScope>(named(APPLICATION_SCOPE))
            val job = scope.coroutineContext[Job]
            requireNotNull(job)
            assertFalse(job.isCancelled)

            koin.unloadModules(listOf(lifecycleModule))

            assertTrue(job.isCancelled)
        } finally {
            stopKoin()
        }
    }

    @Test
    fun `maps datasource close is invoked when module is unloaded`() {
        val userSession = mockk<UserSession>()
        every { userSession.getConferenceFlow() } returns
            MutableStateFlow(FlowResult.Success(Conference.Zero))

        val filesDir = File.createTempFile("maps", "dir").apply {
            delete()
            mkdirs()
        }

        val mapsModule =
            module {
                single {
                    RetrofitMapsDataSource(userSession, filesDir)
                } withOptions {
                    bind<MapsDataSource>()
                    onClose { it?.close() }
                }
            }

        val koin =
            startKoin {
                modules(mapsModule)
            }.koin

        try {
            val dataSource = koin.get<MapsDataSource>()
            assertTrue(dataSource is RetrofitMapsDataSource)

            koin.unloadModules(listOf(mapsModule))

            // A second close must remain safe after DI teardown.
            (dataSource as RetrofitMapsDataSource).close()
        } finally {
            stopKoin()
            filesDir.deleteRecursively()
        }
    }

    @Test
    fun `maps datasource close is idempotent`() {
        val userSession = mockk<UserSession>()
        every { userSession.getConferenceFlow() } returns
            MutableStateFlow(FlowResult.Success(Conference.Zero))

        val filesDir =
            File.createTempFile("maps", "dir").apply {
                delete()
                mkdirs()
            }

        try {
            val subject = RetrofitMapsDataSource(userSession, filesDir)
            subject.close()
            subject.close()
        } finally {
            filesDir.deleteRecursively()
        }
    }
}
