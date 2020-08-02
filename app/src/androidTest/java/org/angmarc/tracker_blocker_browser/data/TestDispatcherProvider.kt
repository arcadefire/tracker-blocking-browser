package org.angmarc.tracker_blocker_browser.data

import kotlinx.coroutines.Dispatchers
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider

class TestDispatcherProvider :
    DispatcherProvider {

    override fun io() = Dispatchers.Main

    override fun main() = Dispatchers.Main

    override fun default() = Dispatchers.Main
}