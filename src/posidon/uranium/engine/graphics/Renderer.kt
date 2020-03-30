package posidon.uranium.engine.graphics

import org.lwjgl.opengl.*
import posidon.library.types.Vec3i
import posidon.uranium.engine.Window
import posidon.uranium.engine.objects.Block
import posidon.uranium.engine.objects.Camera
import posidon.uranium.engine.objects.Chunk
import posidon.uranium.engine.ui.View
import posidon.uranium.engine.objects.Camera.viewMatrix
import posidon.uranium.main.Globals
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList

object Renderer {

    private lateinit var blockShader: Shader
    private lateinit var uiShader: Shader
    val ui = ArrayList<View>()
    val chunks = ChunkMap()
    private val blocksToUpdate = ConcurrentLinkedQueue<Block>()
    private val blocksToFinishUpdating = ConcurrentLinkedQueue<Block>()
    val chunkLock = ReentrantLock()
    val chunksToUpdate = ConcurrentLinkedQueue<Chunk>()

    fun init() {
        blockShader = Shader("/shaders/blockVertex.shade", "/shaders/blockFragment.shade")
        blockShader.create()
        uiShader = Shader("/shaders/viewVertex.shade", "/shaders/viewFragment.shade")
        uiShader.create()
        View.init()
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
    }

    fun render() {
        blockShader.bind()
        blockShader["ambientLight"] = Globals.ambientLight
        blockShader["view"] = viewMatrix
        BlockTextures.sheet.bind()
        for (chunkPos in chunks.keys) {
            val chunk = chunks[chunkPos]!!
            if (chunk.willBeRendered /*&& Camera.isPositionInFov(chunkPos * Chunk.SIZE)*/) {
                blockShader["position"] = (chunk.position * Chunk.SIZE).toVec3f()
                GL30.glBindVertexArray(chunk.mesh!!.vaoId)
                GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, chunk.mesh!!.getVbo(2))
                GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.mesh!!.vertexCount, GL11.GL_UNSIGNED_INT, 0)
            }
        }
        renderUI()
    }

    fun renderUI() {
        uiShader.bind()
        GL30.glBindVertexArray(View.MESH.vaoId)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, View.MESH.getVbo(1))
        for (view in ui)
            if (view.visible)
                view.render(uiShader)
    }

    fun updateData() {
        blockShader.bind()
        blockShader["skyColor"] = Globals.skyColor
        blockShader["projection"] = Window.projectionMatrix

        if (chunkLock.tryLock()) {
            for (i in blocksToUpdate.indices) {
                blocksToUpdate.poll().run {
                    update()
                    if (!Chunk.chunksUpdating.contains(chunk)) Chunk.chunksUpdating.add(chunk)
                }
            }
            Chunk.chunksUpdating.removeIf {
                it.startUpdateMesh()
                true
            }
            chunksToUpdate.removeIf {
                it.finishUpdateMesh()
                true
            }

            chunks.keys.removeIf { chunkPos: Vec3i ->
                if ((chunkPos * Chunk.SIZE - Camera.position.toVec3i()).length > 200) {
                    chunks[chunkPos]!!.clear()
                    true
                } else false
            }
            chunkLock.unlock()
        }
    }

    operator fun set(posInChunk: Vec3i, chunkPos: Vec3i, id: String) {
        chunkLock.lock()
        if (chunks[chunkPos] == null) chunks[chunkPos] = Chunk(chunkPos)
        val cube = chunks[chunkPos]!![posInChunk]
        cube?.chunk?.blockBySides?.get(cube.sides)?.remove(cube)
        chunks[chunkPos]!![posInChunk] =
            if (id.isEmpty()) null
            else Block(id, posInChunk, chunkPos).also { blocksToUpdate.add(it) }
        chunkLock.unlock()
    }

    fun kill() {
        GL20.glUseProgram(0)
        blockShader.destroy()
        uiShader.destroy()
        blocksToUpdate.clear()
        for (key in chunks.keys) {
            chunks[key]!!.clear()
            chunks.remove(key)
        }
        View.destroyAll()
        BlockTextures.clear()
    }

    class ChunkMap : ConcurrentHashMap<Vec3i, Chunk>() {
        operator fun get(x: Int, y: Int, z: Int) = get(Vec3i(x, y, z))
        operator fun set(x: Int, y: Int, z: Int, chunk: Chunk) = set(Vec3i(x, y, z), chunk)
    }
}