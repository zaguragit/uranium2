package posidon.uranium.engine.graphics.mesh

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.*

class UiMesh(
    positions: FloatArray,
    indices: IntArray
) : Mesh() {

    init {
        var posBuffer: FloatBuffer? = null
        var textCoordsBuffer: FloatBuffer? = null
        var indicesBuffer: IntBuffer? = null
        try {
            vertexCount = indices.size
            vaoId = GL30.glGenVertexArrays()
            GL30.glBindVertexArray(vaoId)
            // Position VBO
            var vboId = GL15.glGenBuffers()
            vboIdList.add(vboId)
            posBuffer = MemoryUtil.memAllocFloat(positions.size)
            posBuffer.put(positions).flip()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, posBuffer, GL15.GL_STATIC_DRAW)
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0)
            // Index VBO
            vboId = GL15.glGenBuffers()
            vboIdList.add(vboId)
            indicesBuffer = MemoryUtil.memAllocInt(indices.size)
            indicesBuffer.put(indices).flip()
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId)
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW)

            GL30.glEnableVertexAttribArray(0)
            //GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
            //GL30.glBindVertexArray(0)
        } finally {
            if (posBuffer != null) MemoryUtil.memFree(posBuffer)
            if (textCoordsBuffer != null) MemoryUtil.memFree(textCoordsBuffer)
            if (indicesBuffer != null) MemoryUtil.memFree(indicesBuffer)
        }
    }
}