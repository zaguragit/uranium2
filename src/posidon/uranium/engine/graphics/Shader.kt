package posidon.uranium.engine.graphics

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryUtil
import posidon.library.types.Matrix4f
import posidon.library.types.Vec2f
import posidon.library.types.Vec3f
import posidon.uranium.engine.utils.FileUtils

class Shader(vertexPath: String, fragmentPath: String) {

    private val vertexFile = ShadeLang.shadeToGLSL(FileUtils.loadAsString(vertexPath))
    private val fragmentFile = ShadeLang.shadeToGLSL(FileUtils.loadAsString(fragmentPath))
    private var vertexID = 0
    private var fragmentID = 0
    var programID = 0
        private set

    fun create() {
        programID = GL20.glCreateProgram()
        vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)
        GL20.glShaderSource(vertexID, vertexFile)
        GL20.glCompileShader(vertexID)
        if (GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("[SHADER ERROR - Vertex Shader]: " + GL20.glGetShaderInfoLog(vertexID))
            return
        }
        fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER)
        GL20.glShaderSource(fragmentID, fragmentFile)
        GL20.glCompileShader(fragmentID)
        if (GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("[SHADER ERROR - Fragment Shader]: " + GL20.glGetShaderInfoLog(fragmentID))
            return
        }
        GL20.glAttachShader(programID, vertexID)
        GL20.glAttachShader(programID, fragmentID)
        GL20.glLinkProgram(programID)
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
            System.err.println("[SHADER ERROR - Linking]: " + GL20.glGetProgramInfoLog(programID))
        GL20.glValidateProgram(programID)
        if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE)
            System.err.println("[SHADER ERROR - Validation]: " + GL20.glGetProgramInfoLog(programID))
    }

    inline fun getUniformLocation(name: String) = GL20.glGetUniformLocation(programID, name)

    inline operator fun set(name: String, value: Float) = GL20.glUniform1f(getUniformLocation(name), value)
    inline operator fun set(name: String, value: Int) = GL20.glUniform1i(getUniformLocation(name), value)
    inline operator fun set(name: String, value: Boolean) = GL20.glUniform1i(getUniformLocation(name), if (value) 1 else 0)
    inline operator fun set(name: String, value: Vec2f) = GL20.glUniform2f(getUniformLocation(name), value.x, value.y)
    inline operator fun set(name: String, value: Vec3f) = GL20.glUniform3f(getUniformLocation(name), value.x, value.y, value.z)
    inline operator fun set(name: String, value: Matrix4f) {
        val matrix = MemoryUtil.memAllocFloat(Matrix4f.SIZE * Matrix4f.SIZE)
        matrix.put(value.all).flip()
        GL20.glUniformMatrix4fv(getUniformLocation(name), true, matrix)
        MemoryUtil.memFree(matrix)
    }

    inline fun bind() = GL20.glUseProgram(programID)

    fun destroy() {
        GL20.glDetachShader(programID, vertexID)
        GL20.glDetachShader(programID, fragmentID)
        GL20.glDeleteShader(vertexID)
        GL20.glDeleteShader(fragmentID)
        GL20.glDeleteProgram(programID)
    }
}