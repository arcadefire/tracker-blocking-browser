package org.angmarc.tracker_blocker_browser.exception_report

import androidx.test.core.app.ApplicationProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExceptionEventRecorderTest {

    private val exceptionEventRecorder = ExceptionEventRecorder(
        context = ApplicationProvider.getApplicationContext()
    )

    @Test
    fun `should record the event successfully, and increment the event's counter`() {
        exceptionEventRecorder.record(ExceptionEvent.EXCEPTION_ON_FILE_LOAD_FROM_REMOTE)

        assertThat(exceptionEventRecorder.eventsCount(ExceptionEvent.EXCEPTION_ON_FILE_LOAD_FROM_REMOTE))
            .isEqualTo(1)
    }
}