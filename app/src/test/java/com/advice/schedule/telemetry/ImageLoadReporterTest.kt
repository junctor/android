package com.advice.schedule.telemetry

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.io.IOException

class ImageLoadReporterTest {
    private val crashlytics =
        mockk<FirebaseCrashlytics> {
            every { log(any()) } returns Unit
            every { recordException(any()) } returns Unit
        }

    @Test
    fun `reports failure with url and exception`() {
        val reporter = ImageLoadReporter(crashlytics)
        val error = IOException("handshake failed")

        reporter.report("https://example.com/a.png", error)

        verify(exactly = 1) { crashlytics.log("Image load failed: https://example.com/a.png") }
        verify(exactly = 1) { crashlytics.recordException(error) }
    }

    @Test
    fun `deduplicates repeated failures for the same url`() {
        val reporter = ImageLoadReporter(crashlytics)

        repeat(3) {
            reporter.report("https://example.com/a.png", IOException("boom"))
        }

        verify(exactly = 1) { crashlytics.recordException(any()) }
    }

    @Test
    fun `caps total reports per process`() {
        val reporter = ImageLoadReporter(crashlytics, maxReports = 2)

        repeat(5) { index ->
            reporter.report("https://example.com/$index.png", IOException("boom"))
        }

        verify(exactly = 2) { crashlytics.recordException(any()) }
    }
}
