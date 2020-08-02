package org.angmarc.tracker_blocker_browser.browser

import android.webkit.WebView
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.angmarc.tracker_blocker_browser.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrowserActivityTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(BrowserActivity::class.java, false, false)

    @Test
    fun webViewIsConfigureAsExpected() {
        val activity = rule.launchActivity(null)
        val webView = activity.findViewById<WebView>(R.id.webView)

        webView.post {
            with(webView.settings) {
                assertThat(javaScriptEnabled).isTrue()
                assertThat(loadWithOverviewMode).isTrue()
                assertThat(useWideViewPort).isTrue()
                assertThat(builtInZoomControls).isTrue()
                assertThat(displayZoomControls).isFalse()
                assertThat(supportMultipleWindows()).isTrue()
                assertThat(supportZoom()).isTrue()
            }
        }
    }
}