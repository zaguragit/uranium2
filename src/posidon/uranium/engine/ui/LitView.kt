package posidon.uranium.engine.ui

import org.lwjgl.opengl.GL11
import posidon.uranium.engine.Window
import posidon.uranium.engine.graphics.Shader
import posidon.uranium.engine.graphics.Texture
import posidon.library.types.Matrix4f
import posidon.library.types.Vec2f
import posidon.library.types.Vec3f

abstract class LitView(position: Vec2f, size: Vec2f, background: Texture) : View(position, size, background) {
    override fun render(shader: Shader) {
        background.bind()
        shader["ambientLight"] = Vec3f(1f, 1f, 1f)
        shader["position"] = position
        shader["size"] = Vec2f(size.x / Window.width * Window.height, size.y)
        GL11.glDrawElements(GL11.GL_TRIANGLES, MESH.vertexCount, GL11.GL_UNSIGNED_INT, 0)
    }
}