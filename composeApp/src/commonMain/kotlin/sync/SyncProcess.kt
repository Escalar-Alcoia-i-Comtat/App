package sync

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.until

/**
 * Provides a template for objects to define synchronization routines.
 */
abstract class SyncProcess <Result> {
    sealed class Status {
        data object WAITING: Status()
        open class RUNNING(
            val progress: Float
        ): Status() {
            data object Indeterminate: RUNNING(-1f)

            val isIndeterminate: Boolean = progress < 0f

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as RUNNING

                if (progress != other.progress) return false

                return true
            }

            override fun hashCode(): Int {
                return progress.hashCode()
            }
        }
        data object FINISHED: Status()
    }

    protected val mutableStatus: MutableStateFlow<Status> = MutableStateFlow(Status.WAITING)
    val status: SharedFlow<Status> get() = mutableStatus.asSharedFlow()

    private val mutableResult = MutableStateFlow<Result?>(null)
    val result: Flow<Result?> get() = mutableResult.asSharedFlow()

    /**
     * When called should perform the synchronization process desired.
     */
    protected abstract suspend fun synchronize(): Result

    suspend fun start() {
        val start = Clock.System.now()

        val result = synchronize()
        Napier.v { "Emitting result..." }
        mutableResult.emit(result)

        val end = Clock.System.now()

        Napier.d(tag = "performance") {
            "Task ${this::class.simpleName} took ${start.until(end, DateTimeUnit.MILLISECOND)} ms"
        }
    }
}
