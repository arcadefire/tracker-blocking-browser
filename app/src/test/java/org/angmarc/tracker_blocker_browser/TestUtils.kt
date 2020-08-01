package org.angmarc.tracker_blocker_browser

import androidx.lifecycle.LiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Get the value from a LiveData object. We're waiting for LiveData to emit, for 1 seconds.
 * Once we got a notification via onChanged, we stop observing.
 */
@Throws(InterruptedException::class)
fun <T> getValue(liveData: LiveData<T>): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    liveData.observeForever { o ->
        data[0] = o
        latch.countDown()
    }
    latch.await(1, TimeUnit.SECONDS)

    @Suppress("UNCHECKED_CAST")
    return data[0] as T
}