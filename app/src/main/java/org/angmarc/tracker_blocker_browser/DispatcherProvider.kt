package org.angmarc.tracker_blocker_browser

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

interface DispatcherProvider {

    fun io() : CoroutineContext
    fun main() : CoroutineContext
    fun default() : CoroutineContext
}

class DispatcherProviderImpl : DispatcherProvider {

    override fun io() = Dispatchers.IO
    override fun main() = Dispatchers.Main
    override fun default() = Dispatchers.Default
}

