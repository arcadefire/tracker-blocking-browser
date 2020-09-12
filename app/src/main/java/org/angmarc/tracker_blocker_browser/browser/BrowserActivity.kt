package org.angmarc.tracker_blocker_browser.browser

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.ViewModelProvider
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.TrackerBlockingApplication
import org.angmarc.tracker_blocker_browser.add_allowed_domain.AllowDomainFragmentDialog
import org.angmarc.tracker_blocker_browser.browser.composables.BrowserContent
import org.angmarc.tracker_blocker_browser.browser.composables.BrowserSettings
import org.angmarc.tracker_blocker_browser.core.EventObserver
import org.angmarc.tracker_blocker_browser.stats.StatsDialogFragment
import javax.inject.Inject

private const val FRAGMENT_ADD_TO_ALLOW_LIST = "fragment-add-to-allow-list"
private const val FRAGMENT_VIEW_STATISTICS = "fragment-view-statistics"

class BrowserActivity : AppCompatActivity() {

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

        setContent {
            BrowserContent(viewModel, browserSettings = BrowserSettings(webClient, chromeClient))
        }

        viewModel.messages.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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
        when (item.itemId) {
            R.id.allowList -> viewModel.allowCurrentWebsite()
            R.id.statistics -> viewModel.viewStatistics()
        }
        return super.onOptionsItemSelected(item)
    }
}