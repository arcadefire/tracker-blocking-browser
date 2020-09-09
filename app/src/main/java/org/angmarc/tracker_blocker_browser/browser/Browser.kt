package org.angmarc.tracker_blocker_browser.browser

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BaseTextField
import androidx.compose.foundation.Box
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun Browser(
    url: String?,
    addressBarText: TextFieldValue,
    onAddressChange: (newAddress: TextFieldValue) -> Unit,
    onAddressSubmit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        WebComponent(
            if (url.isNullOrBlank()) "" else url,
            Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
        )
        AddressBar(
            addressBarText,
            onAddressChange,
            onAddressSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(8.dp)
                .clip(
                    shape = RoundedCornerShape(16.dp)
                )
        )
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
    BaseTextField(
        value = addressBarText,
        onValueChange = {
            onAddressChange(it)
        },
        modifier = modifier,
        keyboardType = KeyboardType.Uri,
        imeAction = ImeAction.Send,
        onImeActionPerformed = { onAddressSubmit() }
    )
}

@Composable
fun WebComponent(
    url: String,
    modifier: Modifier,
    webViewClient: WebViewClient = WebViewClient()
) {
    Box(modifier = modifier) {
        AndroidView(::WebView) {
            it.setUrl(url)
            it.webViewClient = webViewClient
        }
    }
}

private fun WebView.setUrl(url: String) {
    loadUrl(url)
}