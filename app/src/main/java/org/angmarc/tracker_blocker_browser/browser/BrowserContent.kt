package org.angmarc.tracker_blocker_browser.browser

import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

data class BrowserSettings(
    val webClient: WebViewClient?,
    val chromeClient: WebChromeClient?
)

@Composable
fun BrowserContent(
    viewModel: BrowserViewModel,
    browserSettings: BrowserSettings
) {
    val state = viewModel.state.observeAsState().value

    Surface {
        Stack(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Browser(
                    url = state?.urlToLoad.orEmpty(),
                    addressBarText = state?.addressBarText ?: TextFieldValue(),
                    onAddressChange = { viewModel.addressBarTextField.value = it },
                    onAddressSubmit = {
                        viewModel.submitAddress()
                    },
                    browserSettings
                )
            }
        }
    }
}