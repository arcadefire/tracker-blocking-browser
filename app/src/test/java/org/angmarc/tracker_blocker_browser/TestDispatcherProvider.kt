package org.angmarc.tracker_blocker_browser

import kotlinx.coroutines.Dispatchers

class TestDispatcherProvider : DispatcherProvider {

    override fun io() = Dispatchers.Main

    override fun main() = Dispatchers.Main

    override fun default() = Dispatchers.Main
}