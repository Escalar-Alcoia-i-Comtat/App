package org.escalaralcoiaicomtat.android

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import org.escalaralcoiaicomtat.app.sync.DataSync

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
): CoroutineWorker(appContext, workerParams) {
    companion object {
        const val OUTPUT_EXCEPTION_TYPE = "EXCEPTION_TYPE"
        const val OUTPUT_EXCEPTION_MESSAGE = "EXCEPTION_MESSAGE"
        const val OUTPUT_EXCEPTION_STACK_TRACE = "EXCEPTION_STACK_TRACE"
    }

    override suspend fun doWork(): Result {
        return try {
            DataSync.start(arguments = inputData.keyValueMap)
            Result.success()
        } catch (e: Exception) {
            Result.failure(
                workDataOf(
                    OUTPUT_EXCEPTION_TYPE to e::class.simpleName,
                    OUTPUT_EXCEPTION_MESSAGE to e.message,
                    OUTPUT_EXCEPTION_STACK_TRACE to e.stackTrace.map { it.toString() },
                )
            )
        }
    }
}
