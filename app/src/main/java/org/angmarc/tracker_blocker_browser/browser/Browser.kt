package org.angmarc.tracker_blocker_browser.browser

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.ui.tooling.preview.Preview

@Preview
@Composable
fun BrowserPreview() {
    Browser(
        url = "https://www.theverge.com",
        address = TextFieldValue("https://www.theverge.com"),
        0,
        {},
        {},
        BrowserSettings(null, null)
    )
}

@Composable
fun Browser(
    url: String?,
    address: TextFieldValue,
    pageLoadProgress: Int = 0,
    onAddressChange: (newAddress: TextFieldValue) -> Unit,
    onAddressSubmit: () -> Unit,
    browserSettings: BrowserSettings
) {
    Column(modifier = Modifier.fillMaxSize()) {
        WebComponent(
            url.orEmpty(),
            Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            browserSettings
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth(pageLoadProgress / 100f)
                .height(2.dp)
                .gravity(Alignment.Start),
            color = Color(255, 0, 0, 100)
        )
        if (pageLoadProgress > 0) {
            Divider(
                modifier = Modifier.height(1.dp).gravity(Alignment.CenterVertically),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
            )
        }
        Box(
            modifier = Modifier
                .height(54.dp)
                .padding(InnerPadding(8.dp))
                .background(
                    color = Color(0xFF333333),
                    shape = RoundedCornerShape(16.dp)
                ),
            gravity = ContentGravity.CenterStart,
        ) {
            AddressBar(
                address,
                onAddressChange,
                onAddressSubmit,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddressBar(
    addressBarText: TextFieldValue,
    onAddressChange: (addressBarText: TextFieldValue) -> Unit,
    onAddressSubmit: () -> Unit,
    modifier: Modifier
) {
    var keyboardController by remember { mutableStateOf<SoftwareKeyboardController?>(null) }

    BaseTextField(
        value = addressBarText,
        onValueChange = {
            onAddressChange(it)
        },
        modifier = modifier,
        textStyle = TextStyle(
            fontSize = TextUnit.Sp(16),
            color = Color(0xFFFFFFFF)
        ),
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Send,
        onImeActionPerformed = {
            onAddressSubmit()
            keyboardController?.hideSoftwareKeyboard()
        },
        onTextInputStarted = { controller -> keyboardController = controller },
    )
}

@Composable
fun WebComponent(
    url: String,
    modifier: Modifier,
    browserSettings: BrowserSettings
) {
    Box(modifier = modifier) {
        AndroidView(::WebView) {
            if (url.isNotEmpty()) {
                it.setUrl(url)
            }
            it.webViewClient = browserSettings.webClient ?: WebViewClient()
            it.webChromeClient = browserSettings.chromeClient
            it.settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportMultipleWindows(true)
                setSupportZoom(true)
            }
        }
    }
}

private fun WebView.setUrl(url: String) {
    loadUrl(url)
}