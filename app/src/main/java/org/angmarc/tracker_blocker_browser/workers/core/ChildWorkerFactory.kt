package org.angmarc.tracker_blocker_browser.workers.core

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

interface ChildWorkerFactory {
    fun build(
        appContext: Context,
        workerParams: WorkerParameters
    ): Worker
}
