package org.angmarc.tracker_blocker_browser.browser

import dagger.Component
import org.angmarc.tracker_blocker_browser.ActivityScope
import org.angmarc.tracker_blocker_browser.ApplicationComponent

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class]
)
interface BrowserActivityComponent {

    fun inject(activity: BrowserActivity)
}