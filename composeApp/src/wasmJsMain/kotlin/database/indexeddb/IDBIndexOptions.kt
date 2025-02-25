package database.indexeddb

external interface IDBIndexOptions {
    var multiEntry: Boolean?
    var unique: Boolean?
}

fun IDBIndexOptions(
    block: IDBIndexOptions.() -> Unit,
): IDBIndexOptions = jso(block)
