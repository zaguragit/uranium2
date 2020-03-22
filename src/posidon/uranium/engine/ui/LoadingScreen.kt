package posidon.uranium.engine.ui

import org.lwjgl.opengl.GL11
import posidon.uranium.engine.Window
import posidon.uranium.engine.graphics.Shader
import posidon.uranium.engine.graphics.Texture
import posidon.library.types.Matrix4f
import posidon.library.types.Vec2f
import posidon.library.types.Vec3f

class LoadingScreen : View(
    Vec2f(0f, 0f),
    Vec2f(2f, 2f),
    Texture("res/textures/ui/loading.png")
) {
    override fun render(shader: Shader?) {
        background.bind()
        shader!!["ambientLight"] = Vec3f(1f, 1f, 1f)
        shader["model"] = if (Window.width > Window.height)
            Matrix4f.transform(position, Vec2f(size.x / Window.width * Window.height, size.y))
            else Matrix4f.transform(position, Vec2f(size.x, size.y / Window.height * Window.width))
        GL11.glDrawElements(GL11.GL_TRIANGLES, MESH.vertexCount, GL11.GL_UNSIGNED_INT, 0)
    }
}