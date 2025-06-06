package org.escalaralcoiaicomtat.app.exception

class TransactionException(cause: Exception? = null) : IDBException("Transaction failed.", cause)
