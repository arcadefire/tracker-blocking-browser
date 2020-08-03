package org.angmarc.tracker_blocker_browser.workers.core

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject

class CustomWorkerFactory @Inject constructor(
    private val workerFactoryMap: Map<String, @JvmSuppressWildcards ChildWorkerFactory>
): WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return (workerFactoryMap[workerClassName] ?: error("Couldn't instantiate a worker factory")).build(
            appContext,
            workerParameters
        )
    }
}