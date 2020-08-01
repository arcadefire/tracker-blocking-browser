package org.angmarc.tracker_blocker_browser.browser.file_loader

import kotlinx.coroutines.Dispatchers
import org.angmarc.tracker_blocker_browser.DispatcherProvider

class TestDispatcherProvider : DispatcherProvider {

    override fun io() = Dispatchers.Main

    override fun main() = Dispatchers.Main

    override fun default() = Dispatchers.Main
}