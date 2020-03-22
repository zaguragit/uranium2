package posidon.uranium.engine.objects

import posidon.library.types.Matrix4f
import posidon.uranium.engine.graphics.mesh.Mesh
import posidon.uranium.engine.graphics.Renderer
import posidon.uranium.engine.graphics.Textures
import posidon.uranium.engine.graphics.mesh.SimpleMesh
import posidon.library.types.Vec3i
import java.util.*

class Block(
    val id: String,
    var posInChunk: Vec3i,
    var chunkPos: Vec3i
) {

    //val absolutePosition inline get() = chunkPos * Chunk.SIZE + posInChunk
    val modelMatrix: Matrix4f = Matrix4f.translate((chunkPos * Chunk.SIZE + posInChunk).toVec3f())
    val mesh inline get() = meshes[sides]
    val texture inline get() = Textures.blockTextures[id]!!
    val chunk inline get() = Renderer.chunks[chunkPos]

    override fun hashCode() = Objects.hash(chunkPos, posInChunk, id) + sides.contentHashCode()

    var sides = BooleanArray(6)
        set(value) {
            val cubesBySides = chunk!!.cubesBySides
            if (cubesBySides.containsKey(sides)) cubesBySides[sides]!!.remove(this)
            field = value
            if (sides[0] || sides[1] || sides[2] || sides[3] || sides[4] || sides[5]) {
                cubesBySides.computeIfAbsent(sides) { ArrayList() }
                cubesBySides[sides]!!.add(this)
                if (!meshes.containsKey(sides)) {
                    val ints = ArrayList<Int>()
                    var i = 0
                    while (i < 36) if (sides[i / 6]) {
                        ints.add(indicesTemplate[i++])
                        ints.add(indicesTemplate[i++])
                        ints.add(indicesTemplate[i++])
                        ints.add(indicesTemplate[i++])
                        ints.add(indicesTemplate[i++])
                        ints.add(indicesTemplate[i++])
                    } else i += 6

                    val vert = ArrayList<Float>()
                    val uv = ArrayList<Float>()
                    var skipI = 0
                    for (j in 0..19) if (ints.contains(j - skipI)) {
                        vert.add(verticesTemplate[j * 3])
                        vert.add(verticesTemplate[j * 3 + 1])
                        vert.add(verticesTemplate[j * 3 + 2])
                        uv.add(uvTemplate[j * 2])
                        uv.add(uvTemplate[j * 2 + 1])
                    } else {
                        for (k in ints.indices) if (ints[k] > j - skipI) ints[k] -= 1
                        skipI++
                    }

                    meshes[field] = SimpleMesh(vert.toFloatArray(), ints.toIntArray(), uv.toFloatArray())

                    vert.clear()
                    ints.clear()
                    uv.clear()
                }
            }
        }

    fun update() {
        try {
            val s = BooleanArray(6)
            s[2] = if (posInChunk.x == Chunk.SIZE - 1)
                Renderer.chunks[chunkPos.copy(x = chunkPos.x + 1)]?.get(posInChunk.copy(x = 0)) == null
            else chunk!![posInChunk.x + 1, posInChunk.y, posInChunk.z] == null
            s[3] = if (posInChunk.x == 0)
                Renderer.chunks[chunkPos.copy(x = chunkPos.x - 1)]?.get(posInChunk.copy(x = Chunk.SIZE - 1)) == null
            else chunk!![posInChunk.x - 1, posInChunk.y, posInChunk.z] == null
            s[1] = if (posInChunk.y == Chunk.SIZE - 1)
                Renderer.chunks[chunkPos.copy(y = chunkPos.y + 1)]?.get(posInChunk.copy(y = 0)) == null
            else chunk!![posInChunk.x, posInChunk.y + 1, posInChunk.z] == null
            s[4] = if (posInChunk.y == 0)
                Renderer.chunks[chunkPos.copy(y = chunkPos.y - 1)]?.get(posInChunk.copy(y = Chunk.SIZE - 1)) == null
            else chunk!![posInChunk.x, posInChunk.y - 1, posInChunk.z] == null
            s[0] = if (posInChunk.z == Chunk.SIZE - 1)
                Renderer.chunks[chunkPos.copy(z = chunkPos.z + 1)]?.get(posInChunk.copy(z = 0)) == null
            else chunk!![posInChunk.x, posInChunk.y, posInChunk.z + 1] == null
            s[5] = if (posInChunk.z == 0)
                Renderer.chunks[chunkPos.copy(z = chunkPos.z - 1)]?.get(posInChunk.copy(z = Chunk.SIZE - 1)) == null
            else chunk!![posInChunk.x, posInChunk.y, posInChunk.z - 1] == null
            sides = s
        } catch (e: Exception) {
            e.printStackTrace()
            println("[ERROR]: something weird going on here!")
            sides = booleanArrayOf(true, true, true, true, true, true)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Block
        if (id != other.id) return false
        if (chunkPos != other.chunkPos) return false
        if (posInChunk != other.posInChunk) return false
        if (!sides.contentEquals(other.sides)) return false
        return true
    }

    companion object {

        var meshes = HashMap<BooleanArray, Mesh?>()
        fun kill() = meshes.clear()

        val indicesTemplate = intArrayOf(
            0, 1, 3, 3, 1, 2,  // Front
            8, 10, 11, 9, 8, 11,  // Top
            12, 13, 7, 5, 12, 7,  // Right
            6, 14, 4, 6, 15, 14,  // Left
            16, 18, 19, 17, 16, 19,  // Bottom
            7, 4, 5, 7, 6, 4 // Back
        )
        val verticesTemplate = floatArrayOf(
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f
        )
        val uvTemplate = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
        )
    }
}