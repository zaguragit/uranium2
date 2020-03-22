package posidon.uranium.engine.graphics.mesh

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import java.util.*

abstract class Mesh {

    var vaoId = 0
    var vertexCount = 0

    protected var vboIdList = ArrayList<Int>()

    fun getVbo(i: Int): Int = vboIdList[i]

    fun delete() {
        GL20.glDisableVertexAttribArray(0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        for (vboId in vboIdList) GL15.glDeleteBuffers(vboId)
        GL30.glBindVertexArray(0)
        GL30.glDeleteVertexArrays(vaoId)
    }
}