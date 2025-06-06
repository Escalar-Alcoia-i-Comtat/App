package org.escalaralcoiaicomtat.app.database

const val DATABASE_NAME = "escalar-alcoia-i-comtat"
const val DATABASE_VERSION = 2

expect object Database {
    fun open()
}
