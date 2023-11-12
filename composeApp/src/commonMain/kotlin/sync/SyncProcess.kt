package sync

/**
 * Provides a template for objects to define synchronization routines.
 */
interface SyncProcess {
    /**
     * When called should perform the synchronization process desired.
     */
    suspend fun synchronize()
}
