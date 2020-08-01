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
import org.angmarc.tracker_blocker_browser.TrackerBlockingApplication
import org.angmarc.tracker_blocker_browser.core.EventObserver
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.allowed_list.AllowDomainFragmentDialog
import org.angmarc.tracker_blocker_browser.databinding.ActivityBrowserBinding
import org.angmarc.tracker_blocker_browser.extensions.hideKeyboard
import org.angmarc.tracker_blocker_browser.stats.StatsDialogFragment
import javax.inject.Inject

private const val FRAGMENT_ADD_TO_ALLOW_LIST = "fragment-add-to-allow-list"
private const val FRAGMENT_VIEW_STATISTICS = "fragment-view-statistics"

class BrowserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBrowserBinding

    @Inject
    lateinit var webClient: BrowserWebClient

    private val viewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerBrowserActivityComponent
            .builder()
            .applicationComponent((application as TrackerBlockingApplication).applicationComponent)
            .build()
            .inject(this)

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
        viewModel.allowWebsiteClicks.observe(this,
            EventObserver {
                AllowDomainFragmentDialog
                    .instance(domainNameToAllow = it)
                    .show(supportFragmentManager, FRAGMENT_ADD_TO_ALLOW_LIST)
            })
        viewModel.statisticsClicks.observe(this,
            EventObserver {
                StatsDialogFragment
                    .instance()
                    .show(supportFragmentManager, FRAGMENT_VIEW_STATISTICS)
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.browser_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.allowList -> viewModel.allowCurrentWebsite()
            R.id.statistics -> viewModel.viewStatistics()
        }
        return super.onOptionsItemSelected(item)
    }
}