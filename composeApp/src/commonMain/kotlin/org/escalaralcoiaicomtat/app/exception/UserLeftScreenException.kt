package org.escalaralcoiaicomtat.app.exception

import kotlinx.coroutines.CancellationException

class UserLeftScreenException(message: String? = null) : CancellationException(message)
