package org.angmarc.tracker_blocker_browser.browser

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class PageLoadProgressTest {

    private val pageLoadListener = mock<PageLoadListener>()
    private val pageLoadProgress = PageLoadProgress()

    @Test
    fun `should call the expected listener callback, when the progress is updated` (){
        pageLoadProgress.pageLoadListener = pageLoadListener

        pageLoadProgress.progressChanged(20)

        verify(pageLoadListener).onProgress(20)
    }
}