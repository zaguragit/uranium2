package posidon.library.types

data class Tuple<A, B>(var a: A, var b: B) {
    fun set(tuple: Tuple<A, B>) {
        a = tuple.a
        b = tuple.b
    }
    override fun toString() = "($a, $b)"
}