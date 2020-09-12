package org.angmarc.tracker_blocker_browser.browser.composables

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.animate
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.gravity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.ui.tooling.preview.Preview
import org.angmarc.tracker_blocker_browser.R

@Preview
@Composable
fun BrowserPreview() {
    Browser(
        url = "https://www.theverge.com",
        0,
        true,
        {},
        {},
        {},
        BrowserSettings(null, null)
    )
}

@Composable
fun Browser(
    url: String?,
    pageLoadProgress: Int = 0,
    isBlockingSuspended: Boolean,
    onAddressChange: (newAddress: TextFieldValue) -> Unit,
    onAddressSubmit: () -> Unit,
    onSettingsClick: () -> Unit,
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
        if (pageLoadProgress > 0) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth(pageLoadProgress / 100f)
                    .height(2.dp)
                    .gravity(Alignment.Start),
                color = Color(255, 0, 0, 100)
            )
        }
        Divider(
            modifier = Modifier.height(1.dp).gravity(Alignment.CenterVertically),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
        )
        Surface {
            Row(
                modifier = Modifier
                    .padding(InnerPadding(4.dp))
                    .preferredHeight(42.dp),
            ) {
                AddressBar(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(
                            color = Color(0xFF333333),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    isBlockingSuspended,
                    onAddressChange,
                    onAddressSubmit
                )

                Box(
                    gravity = ContentGravity.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    TextButton(
                        modifier = Modifier
                            .wrapContentHeight()
                            .preferredWidth(32.dp),
                        onClick = { onSettingsClick() }
                    ) {
                        Image(
                            modifier = Modifier.preferredSize(32.dp, 32.dp),
                            asset = vectorResource(id = R.drawable.ic_more_vert_24px),
                            contentScale = ContentScale.None
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressBar(
    modifier: Modifier,
    isBlockingSuspended: Boolean,
    onAddressChange: (newAddress: TextFieldValue) -> Unit,
    onAddressSubmit: () -> Unit
) {
    Box(
        modifier = modifier,
        gravity = ContentGravity.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isBlockingSuspended) {
                AddressBarIcon(Modifier.padding(start = 6.dp), isBlockingSuspended)
            }

            val padding: Dp = animate(
                target = 8.dp,
                animSpec = TweenSpec(
                    easing = FastOutSlowInEasing,
                    durationMillis = 700
                )
            )

            AddressBarTextField(
                onAddressChange,
                onAddressSubmit,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(start = padding)
                    .weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddressBarTextField(
    onAddressChange: (addressBarText: TextFieldValue) -> Unit,
    onAddressSubmit: () -> Unit,
    modifier: Modifier
) {
    var keyboardController by remember { mutableStateOf<SoftwareKeyboardController?>(null) }
    var fieldValue by remember { mutableStateOf(TextFieldValue("")) }

    BaseTextField(
        value = fieldValue,
        onValueChange = {
            fieldValue = it
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
                domStorageEnabled = true
                databaseEnabled = false
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportMultipleWindows(true)
                setSupportZoom(true)
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            }
        }
    }
}

private fun WebView.setUrl(url: String) {
    loadUrl(url)
}