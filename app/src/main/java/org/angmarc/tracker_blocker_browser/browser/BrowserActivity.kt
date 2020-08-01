package org.angmarc.tracker_blocker_browser.browser

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.angmarc.tracker_blocker_browser.BrowserApplication
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.allowed_list.AllowListFragmentDialog
import org.angmarc.tracker_blocker_browser.databinding.ActivityBrowserBinding
import org.angmarc.tracker_blocker_browser.extensions.hideKeyboard
import javax.inject.Inject

private const val FRAGMENT_ADD_TO_ALLOW_LIST = "fragment-add-to-allow-list"

class BrowserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBrowserBinding

    @Inject
    lateinit var webClient: BrowserWebClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerBrowserActivityComponent
            .builder()
            .applicationComponent((application as BrowserApplication).applicationComponent)
            .build()
            .inject(this)

        val viewModel: BrowserViewModel by viewModels()
        binding = ActivityBrowserBinding.inflate(LayoutInflater.from(this), null, false)

        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        val webView = findViewById<WebView>(R.id.webView)
        webView.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportMultipleWindows(true)
            setSupportZoom(true)
        }
        webView.webViewClient = webClient
        webView.webChromeClient = WebChromeClient()

        binding.addressInput.setOnEditorActionListener { textView, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                viewModel.addressBarText.value = binding.addressInput.text.toString()
            }
            true
        }

        viewModel.url.observe(this, Observer {
            webView.loadUrl(it)
            hideKeyboard()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.browser_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.allowList) {
            AllowListFragmentDialog.instance().show(supportFragmentManager, FRAGMENT_ADD_TO_ALLOW_LIST)
        }
        return super.onOptionsItemSelected(item)
    }
}