package posidon.uranium.engine.objects

import posidon.library.types.Vec3i
import posidon.uranium.engine.graphics.BlockTextures
import posidon.uranium.engine.graphics.Renderer
import posidon.uranium.engine.graphics.mesh.SimpleMesh
import java.util.*
import kotlin.concurrent.thread

class Chunk(val position: Vec3i) {

    private val blocks = arrayOfNulls<Block>(CUBE_SIZE)

    operator fun get(pos: Vec3i) = blocks[pos.x * SIZE * SIZE + pos.y * SIZE + pos.z]
    operator fun get(x: Int, y: Int, z: Int) = blocks[x * SIZE * SIZE + y * SIZE + z]
    operator fun set(pos: Vec3i, block: Block?) { blocks[pos.x * SIZE * SIZE + pos.y * SIZE + pos.z] = block }

    val blockBySides = HashMap<BooleanArray, MutableList<Block>>()
    fun clear() = blockBySides.clear()

    companion object {
        const val SIZE = 16
        const val CUBE_SIZE = SIZE * SIZE * SIZE
        val chunksUpdating = LinkedList<Chunk>()
    }

    var willBeRendered = false

    var mesh: SimpleMesh? = null
        private set

    var isFull = false
        private set

    private lateinit var tmpVertices: FloatArray
    private lateinit var tmpIndices: IntArray
    private lateinit var tmpUv: FloatArray

    fun finishUpdateMesh() {
        val oldMesh = mesh
        mesh = SimpleMesh(tmpVertices, tmpIndices, tmpUv)
        willBeRendered = Renderer.chunks[position.copy(x = position.x + 1)]?.isFull != true ||
                Renderer.chunks[position.copy(x = position.x - 1)]?.isFull != true ||
                Renderer.chunks[position.copy(x = position.y + 1)]?.isFull != true ||
                Renderer.chunks[position.copy(x = position.y - 1)]?.isFull != true ||
                Renderer.chunks[position.copy(x = position.z + 1)]?.isFull != true ||
                Renderer.chunks[position.copy(x = position.z - 1)]?.isFull != true
        oldMesh?.delete()
    }

    fun startUpdateMesh() {
        thread {
            val ints = ArrayList<Int>()
            val vert = ArrayList<Float>()
            val uv = ArrayList<Float>()

            var minIndex = 0

            var full = true

            for (block in blocks) {
                if (block == null) full = false
                else {
                    val vertexArray = Block.vertexArrays[block.sides]!!
                    for (i in 0 until vertexArray.size / 3) {
                        vert.add(vertexArray[i * 3] + block.posInChunk.x)
                        vert.add(vertexArray[i * 3 + 1] + block.posInChunk.y)
                        vert.add(vertexArray[i * 3 + 2] + block.posInChunk.z)
                    }
                    val uvArray = Block.uvArrays[block.sides]!!
                    val blockUv = BlockTextures.getUvForId(block.id)
                    for (i in 0 until uvArray.size / 2) {
                        uv.add((uvArray[i * 2] + blockUv.x) / BlockTextures.WIDTH)
                        uv.add((uvArray[i * 2 + 1] + blockUv.y) / BlockTextures.HEIGHT)
                    }
                    for (index in Block.indexArrays[block.sides]!!) ints.add(index + minIndex)
                    minIndex = uv.size / 2
                }
            }

            tmpVertices = vert.toFloatArray()
            tmpIndices = ints.toIntArray()
            tmpUv = uv.toFloatArray()

            Renderer.chunksToUpdate.add(this)
            isFull = full

            vert.clear()
            ints.clear()
            uv.clear()
        }
    }
}