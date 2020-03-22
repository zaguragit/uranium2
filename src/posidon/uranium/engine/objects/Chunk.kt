package posidon.uranium.engine.objects

import posidon.library.types.Vec3i
import posidon.uranium.engine.graphics.Renderer
import java.util.*

class Chunk(val position: Vec3i) {

    private val blocks = arrayOfNulls<Block>(CUBE_SIZE)

    operator fun get(pos: Vec3i) = blocks[pos.x * SIZE * SIZE + pos.y * SIZE + pos.z]
    operator fun get(x: Int, y: Int, z: Int) = blocks[x * SIZE * SIZE + y * SIZE + z]
    operator fun set(pos: Vec3i, block: Block?) { blocks[pos.x * SIZE * SIZE + pos.y * SIZE + pos.z] = block }

    val cubesBySides = HashMap<BooleanArray, MutableList<Block>>()
    fun clear() = cubesBySides.clear()

    companion object {
        const val SIZE = 16
        const val CUBE_SIZE = SIZE * SIZE * SIZE
    }
}