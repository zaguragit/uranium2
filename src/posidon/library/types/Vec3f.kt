package posidon.library.types

import java.util.*
import kotlin.math.sqrt

data class Vec3f(var x: Float, var y: Float, var z: Float) {

    fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec3f) return false
        return other.x.compareTo(x) == 0 && other.y.compareTo(y) == 0 && other.z.compareTo(z) == 0
    }

    override fun hashCode() = Objects.hash(x, y, z)
    override fun toString() = "vec3f($x, $y, $z)"
    fun toVec3i() = Vec3i(x.toInt(), y.toInt(), z.toInt())

    companion object {
        fun blend(v1: Vec3f, v2: Vec3f, ratio: Float): Vec3f {
            val inverseRation = 1f - ratio
            val r = v1.x * ratio + v2.x * inverseRation
            val g = v1.y * ratio + v2.y * inverseRation
            val b = v1.z * ratio + v2.z * inverseRation
            return Vec3f(r, g, b)
        }
    }

    operator fun plus(other: Vec3f) =
        Vec3f(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3f) =
        Vec3f(x - other.x, y - other.y, z - other.z)
    operator fun times(other: Vec3f) =
        Vec3f(x * other.x, y * other.y, z * other.z)
    operator fun times(other: Float) = Vec3f(x * other, y * other, z * other)
    operator fun div(other: Vec3f) =
        Vec3f(x / other.x, y / other.y, z / other.z)
    operator fun div(float: Float) = Vec3f(x / float, y / float, z / float)
    val length get() = sqrt(x * x + y * y + (z * z).toDouble()).toFloat()
    fun normalize() = this / length
    fun dot(other: Vec3f) = x * other.x + y * other.y + z * other.z
}