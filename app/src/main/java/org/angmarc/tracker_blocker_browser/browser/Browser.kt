package org.angmarc.tracker_blocker_browser.browser

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        addressBarText = TextFieldValue("https://www.theverge.com"),
        {},
        {}
    )
}

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
        Divider(
            modifier = Modifier.height(1.dp).gravity(Alignment.CenterVertically),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
        )
        Box(
            modifier = Modifier
                .height(54.dp)
                .padding(InnerPadding(8.dp))
                .background(
                    color = Color(0xFFCCCCCC),
                    shape = RoundedCornerShape(16.dp)
                ),
            gravity =  ContentGravity.CenterStart,
        ) {
            AddressBar(
                addressBarText,
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
    BaseTextField(
        value = addressBarText,
        onValueChange = {
            onAddressChange(it)
        },
        modifier = modifier,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Send,
        onImeActionPerformed = { onAddressSubmit() },
        textStyle = TextStyle(
            fontSize = TextUnit.Sp(16),
            color = Color(0xFFFFFFFF)
        )
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