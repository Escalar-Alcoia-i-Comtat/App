package database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBTransaction/durability */
external interface IDBTransactionOptions {
    val durability: String
}

enum class IDBTransactionDurability(
    val value: String,
) {
    Default("default"),
    Strict("strict"),
    Relaxed("relaxed"),
}

private fun IDBTransactionOptions(durability: String): IDBTransactionOptions =
    js("({ durability: durability })")

fun IDBTransactionOptions(durability: IDBTransactionDurability): IDBTransactionOptions =
    IDBTransactionOptions(durability.value)
