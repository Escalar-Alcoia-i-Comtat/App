package sync

fun interface Preprocessor<Type : Any> {
    operator fun invoke(value: Type): Type
}
