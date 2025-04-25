package org.escalaralcoiaicomtat.app.data.editable

interface Editable<NonEditable : Any> {
    fun validate(): Boolean

    fun build(): NonEditable
}
