package posidon.uranium.engine.graphics

import org.lwjgl.opengl.*
import posidon.library.types.Matrix4f
import posidon.library.types.Vec3i
import posidon.uranium.engine.Window
import posidon.uranium.engine.graphics.mesh.SimpleMesh
import posidon.uranium.engine.objects.Block
import posidon.uranium.engine.objects.Camera
import posidon.uranium.engine.objects.Chunk
import posidon.uranium.engine.ui.View
import posidon.library.types.Tuple
import posidon.uranium.engine.objects.Camera.viewMatrix
import posidon.uranium.main.Globals
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList

object Renderer {

    private var blockShader: Shader? = null
    private var uiShader: Shader? = null
    val ui = ArrayList<View?>()
    val chunks = ChunkMap()
    private val blockQueue = ConcurrentLinkedQueue<Tuple<Tuple<Vec3i, Vec3i>, String>>()
    val chunkLock = ReentrantLock()

    fun init() {
        blockShader = Shader("/shaders/blockVertex.shade", "/shaders/blockFragment.shade")
        blockShader!!.create()
        uiShader = Shader("/shaders/viewVertex.shade", "/shaders/viewFragment.shade")
        uiShader!!.create()
        View.init()
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        Block.meshes[booleanArrayOf(true, true, true, true, true, true)] = SimpleMesh(Block.verticesTemplate, Block.indicesTemplate, Block.uvTemplate)
    }

    fun render() {
        blockShader!!.bind()
        blockShader!!["ambientLight"] = Globals.ambientLight
        blockShader!!["view"] = viewMatrix
        for (chunkPos in chunks.keys) if (Camera.isPositionInFov(chunkPos * Chunk.SIZE)) {
            val chunk = chunks[chunkPos]!!
            for (sides in chunk.cubesBySides.keys) {
                GL30.glBindVertexArray(Block.meshes[sides]!!.vaoId)
                GL30.glEnableVertexAttribArray(0)
                GL30.glEnableVertexAttribArray(1)
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, Block.meshes[sides]!!.getVbo(2))
                for (block in chunk.cubesBySides[sides]!!) {
                    block.texture.bind()
                    blockShader!!["model"] = block.modelMatrix
                    GL11.glDrawElements(GL11.GL_TRIANGLES, block.mesh!!.vertexCount, GL11.GL_UNSIGNED_INT, 0)
                }
            }
        }
        uiShader!!.bind()
        GL30.glBindVertexArray(View.MESH.vaoId)
        GL30.glEnableVertexAttribArray(0)
        GL30.glEnableVertexAttribArray(1)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, View.MESH.getVbo(2))
        for (view in ui) if (view!!.visible) view.render(uiShader)
    }

    fun updateData() {
        blockShader!!.bind()
        blockShader!!["skyColor"] = Globals.skyColor
        blockShader!!["projection"] = Window.projectionMatrix
        val blocks = ArrayList<Block>()
        for (i in blockQueue.indices) {
            val tuple = blockQueue.poll()
            actuallySetBlock(tuple.b, tuple.a)?.run { blocks.add(this) }
        }
        for (block in blocks) block.update()

        chunks.keys.removeIf { chunkPos: Vec3i ->
            if ((chunkPos * Chunk.SIZE - Camera.position.toVec3i()).length > 200) {
                chunks[chunkPos]!!.clear()
                true
            } else false
        }
    }

    fun bg() {
        chunkLock.lock()
        chunkLock.unlock()
    }

    operator fun set(posInChunk: Vec3i, chunkPos: Vec3i, id: String) {
        blockQueue.add(Tuple(Tuple(posInChunk, chunkPos), id))
    }

    private fun actuallySetBlock(id: String, positions: Tuple<Vec3i, Vec3i>): Block? {
        if (chunks[positions.b] == null) chunks[positions.b] = Chunk(positions.b)
        val cube = chunks[positions.b]!![positions.a]
        cube?.chunk?.cubesBySides?.get(cube.sides)?.remove(cube)
        return (if (id.isEmpty()) null else Block(id, positions.a, positions.b))
            .also { chunks[positions.b]!![positions.a] = it }
    }

    fun kill() {
        GL20.glUseProgram(0)
        blockShader!!.kill()
        uiShader!!.kill()
        for (chunk in chunks.values) chunk.clear()
        chunks.clear()
        blockQueue.clear()
        ui.clear()
        Block.kill()
        Textures.clear()
    }

    class ChunkMap : ConcurrentHashMap<Vec3i, Chunk>() {
        operator fun get(x: Int, y: Int, z: Int) = get(Vec3i(x, y, z))
        operator fun set(x: Int, y: Int, z: Int, chunk: Chunk) = set(Vec3i(x, y, z), chunk)
    }
}