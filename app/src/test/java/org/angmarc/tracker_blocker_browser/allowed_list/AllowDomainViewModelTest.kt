package org.angmarc.tracker_blocker_browser.allowed_list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.TestDispatcherProvider
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomain
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.BreakageType
import org.angmarc.tracker_blocker_browser.getValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class AllowDomainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val allowedDomainsDao = mock<AllowedDomainsDao>()

    private val viewModel = AllowDomainViewModel(
        "a domain",
        allowedDomainsDao,
        TestDispatcherProvider()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should save the allowed website with the selected breakage-type when the add button is clicked`() {
        viewModel.selectedRadio.value = R.id.navigationButton

        viewModel.addAllowedDomain()

        val expectedAllowedDomain = AllowedDomain("a domain", BreakageType.BROKEN_NAVIGATION)
        verify(allowedDomainsDao).insert(expectedAllowedDomain)
    }

    @Test
    fun `should save the allowed website with the default breakage-type when the add button is clicked`() {
        viewModel.addAllowedDomain()

        val expectedAllowedDomain = AllowedDomain("a domain", BreakageType.VIDEOS_DONT_LOAD)
        verify(allowedDomainsDao).insert(expectedAllowedDomain)
    }

    @Test
    fun `should signal the view that the domain has been added, when the database insertion is completed`() {
        viewModel.addAllowedDomain()

        val expectedAllowedDomain = AllowedDomain("a domain", BreakageType.VIDEOS_DONT_LOAD)
        verify(allowedDomainsDao).insert(expectedAllowedDomain)

        assertThat(getValue(viewModel.onAllowWebsiteAdded).peekContent()).isNotNull
    }
}