package posidon.uranium.engine.graphics

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

class Texture(filename: String?) {

    val id: Int = loadTexture(filename)

    fun bind() = GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
    fun delete() = GL11.glDeleteTextures(id)

    companion object {
        private fun loadTexture(path: String?): Int {
            var width = 0
            var height = 0
            var buf: ByteBuffer? = null
            MemoryStack.stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                val channels = stack.mallocInt(1)
                buf = STBImage.stbi_load(path, w, h, channels, 4)
                //if (buf == null) throw new NoSuchFileException("Texture not loaded: [" + path + "] " + stbi_failure_reason());
                width = w.get()
                height = h.get()
            }
            val textureId = GL11.glGenTextures()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf)
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.5f)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE)
            STBImage.stbi_image_free(buf!!)
            return textureId
        }
    }
}