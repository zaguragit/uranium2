package posidon.uranium.engine.ui

import org.lwjgl.opengl.GL11
import posidon.uranium.engine.Window
import posidon.uranium.engine.graphics.mesh.Mesh
import posidon.uranium.engine.graphics.Renderer
import posidon.uranium.engine.graphics.Shader
import posidon.uranium.engine.graphics.Texture
import posidon.library.types.Matrix4f
import posidon.library.types.Vec2f
import posidon.uranium.engine.graphics.mesh.UiMesh
import posidon.uranium.main.Globals

abstract class View(var position: Vec2f, var size: Vec2f, protected var background: Texture) {

    var visible = true

    fun setBackgroundPath(path: String?) {
        background.delete()
        background = Texture(path)
    }

    fun destroy() {
        Renderer.ui.remove(this)
        background.delete()
    }

    open fun render(shader: Shader) {
        background.bind()
        shader["ambientLight"] = Globals.ambientLight
        shader["position"] = position
        shader["size"] = Vec2f(size.x / Window.width * Window.height, size.y)
        GL11.glDrawElements(GL11.GL_TRIANGLES, MESH.vertexCount, GL11.GL_UNSIGNED_INT, 0)
    }

    companion object {
        lateinit var MESH: Mesh private set

        fun init() {
            MESH = UiMesh(floatArrayOf(
                -0.5f, 0.5f,
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f
            ), intArrayOf(0, 1, 3, 3, 1, 2))

            /*
            MESH = SimpleMesh(floatArrayOf(
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f
            ), intArrayOf(0, 1, 3, 3, 1, 2), floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f))*/
        }

        fun destroyAll() {
            for (view in Renderer.ui) view.destroy()
        }
    }

    init { Renderer.ui.add(this) }
}