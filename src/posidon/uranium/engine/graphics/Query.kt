package posidon.uranium.engine.graphics

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15

class Query(val type: Int) {

    val id = GL15.glGenQueries()
    var inUse = false
        private set

    fun start() {
        inUse = true
        GL15.glBeginQuery(type, id)
    }
    inline fun end() = GL15.glEndQuery(type)

    fun getResult(): Int {
        inUse = false
        return GL15.glGetQueryObjecti(id, GL15.GL_QUERY_RESULT)
    }

    inline val isResultReady get() = GL15.glGetQueryObjecti(id, GL15.GL_QUERY_RESULT_AVAILABLE) == GL11.GL_TRUE
}