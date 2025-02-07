package sync

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.until

/**
 * Provides a template for objects to define synchronization routines.
 */
abstract class SyncProcess {
    sealed class Status {
        data object WAITING : Status()
        open class RUNNING(
            val progress: Float
        ) : Status() {
            data object Indeterminate : RUNNING(-1f)

            val isIndeterminate: Boolean = progress < 0f

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as RUNNING

                return progress == other.progress
            }

            override fun hashCode(): Int {
                return progress.hashCode()
            }
        }

        data object FINISHED : Status()
    }

    protected val mutableStatus: MutableStateFlow<Status> = MutableStateFlow(Status.WAITING)
    val status: SharedFlow<Status> get() = mutableStatus.asSharedFlow()

    interface SyncContext {
        val arguments: Map<String, Any>

        fun getString(key: String): String? = if (!arguments.containsKey(key)) {
            null
        } else {
            arguments[key] as? String
        }
    }

    /**
     * When called should perform the synchronization process desired.
     */
    protected abstract suspend fun SyncContext.synchronize()

    suspend fun start(arguments: Map<String, Any?>? = null) {
        val start = Clock.System.now()

        val args = arguments.orEmpty()
            .toList()
            .mapNotNull { (key, value) -> value?.let { key to value } }
            .toMap()

        with(
            object : SyncContext {
                override val arguments: Map<String, Any> = args
            }
        ) { synchronize() }

        val end = Clock.System.now()

        Napier.d(tag = "performance") {
            "Task ${this::class.simpleName} took ${start.until(end, DateTimeUnit.MILLISECOND)} ms"
        }
    }
}
