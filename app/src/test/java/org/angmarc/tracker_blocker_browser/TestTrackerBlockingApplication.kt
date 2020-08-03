package org.angmarc.tracker_blocker_browser

import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method

class TestTrackerBlockingApplication : TrackerBlockingApplication(), TestLifecycleApplication {

    override fun onCreate() {}
    override fun beforeTest(method: Method) {}
    override fun prepareTest(test: Any) {}
    override fun afterTest(method: Method) {}
}