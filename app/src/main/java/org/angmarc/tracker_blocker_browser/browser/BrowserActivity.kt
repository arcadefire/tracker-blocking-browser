package org.angmarc.tracker_blocker_browser.browser

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.TrackerBlockingApplication
import org.angmarc.tracker_blocker_browser.add_allowed_domain.AllowDomainFragmentDialog
import org.angmarc.tracker_blocker_browser.core.EventObserver
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

    @Inject
    lateinit var chromeClient: BrowserWebChomeClient

    @Inject
    lateinit var viewModelProvider: ViewModelProvider

    private val viewModel: BrowserViewModel by lazy {
        viewModelProvider.get(BrowserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerBrowserActivityComponent
            .builder()
            .applicationComponent((application as TrackerBlockingApplication).applicationComponent)
            .viewModelStoreOwner(this)
            .build()
            .inject(this)

        binding = ActivityBrowserBinding.inflate(LayoutInflater.from(this), null, false)

        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))

        configureWebView()

        binding.addressInput.setOnEditorActionListener { _, _, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                viewModel.addressBarText.value = binding.addressInput.text.toString()
            }
            true
        }

        viewModel.state.observe(this, Observer {
            it.urlToLoad?.let { urlToLoad ->
                binding.webView.loadUrl(urlToLoad)
                hideKeyboard()
            }
            if (it.shouldSuspendBlocking) {
                binding.addressInputLayout.setStartIconDrawable(R.drawable.ic_remove_circle_outline_24px)
            } else {
                binding.addressInputLayout.startIconDrawable = null
            }
        })
        viewModel.loadingState.observe(this, Observer {
            with(binding.pageLoadingProgressBar) {
                visibility = if (it.shouldShowProgress) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                progress = it.progress
            }
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

    private fun configureWebView() {
        with(binding.webView) {
            settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportMultipleWindows(true)
                setSupportZoom(true)
            }
            webViewClient = webClient
            webChromeClient = chromeClient
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.browser_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.allowList -> viewModel.allowCurrentWebsite()
            R.id.statistics -> viewModel.viewStatistics()
        }
        return super.onOptionsItemSelected(item)
    }
}