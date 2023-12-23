package exception

import kotlinx.coroutines.CancellationException

class UserLeftScreenException(message: String? = null) : CancellationException(message)
