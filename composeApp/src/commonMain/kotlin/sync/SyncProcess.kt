package sync

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.until

/**
 * Provides a template for objects to define synchronization routines.
 */
abstract class SyncProcess {
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

    protected val mutableStatus: MutableState<Status> = mutableStateOf(Status.WAITING)

    val status: State<Status> get() = mutableStatus

    /**
     * When called should perform the synchronization process desired.
     */
    protected abstract suspend fun synchronize()

    suspend fun start() {
        val start = Clock.System.now()

        synchronize()

        val end = Clock.System.now()

        Napier.d(tag = "performance") {
            "Task ${this::class.simpleName} took ${start.until(end, DateTimeUnit.MILLISECOND)} ms"
        }
    }
}
