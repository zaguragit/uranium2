package posidon.uranium.engine

import org.lwjgl.glfw.*
import posidon.library.types.Vec2f
import posidon.uranium.engine.objects.Camera

class Input(w: Window) {

    val keyListener: GLFWKeyCallback
    val cursorListener: GLFWCursorPosCallback
    val mouseButtonListener: GLFWMouseButtonCallback
    val scrollListener: GLFWScrollCallback

    private var oldCurX = 0.0
    private var oldCurY = 0.0

    fun kill() {
        keyListener.free()
        cursorListener.free()
        mouseButtonListener.free()
        scrollListener.free()
    }

    companion object {
        private val keys = BooleanArray(GLFW.GLFW_KEY_LAST)
        private val mouseButtons = BooleanArray(GLFW.GLFW_MOUSE_BUTTON_LAST)
        var curX = 0.0
        var curY = 0.0
        var scrollX = 0.0
        var scrollY = 0.0
        fun isKeyDown(key: Int): Boolean = keys[key]
        fun isButtonDown(btn: Int): Boolean = mouseButtons[btn]
    }

    init {
        keyListener = object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scanCode: Int, action: Int, mods: Int) {
                keys[key] = action != GLFW.GLFW_RELEASE
                when (key) {
                    GLFW.GLFW_KEY_F11 -> w.isFullscreen = !w.isFullscreen
                    GLFW.GLFW_KEY_ESCAPE -> w.mouseLocked = false
                }
            }
        }
        cursorListener = object : GLFWCursorPosCallback() {
            override fun invoke(window: Long, x: Double, y: Double) {
                if (Window.mouseLocked) {
                    curX = x
                    curY = y

                    val dx = (curX - oldCurX).toFloat()
                    val dy = (curY - oldCurY).toFloat()
                    Camera.rotation += Vec2f(-Camera.sensitivity * dy, -Camera.sensitivity * dx)
                    if (Camera.rotation.x > 90) Camera.rotation.x = 90f
                    else if (Camera.rotation.x < -90) Camera.rotation.x = -90f
                    if (Camera.rotation.y > 360) Camera.rotation.y -= 360f
                    else if (Camera.rotation.y < 0) Camera.rotation.y += 360f

                    oldCurX = curX
                    oldCurY = curY
                }
            }
        }
        mouseButtonListener = object : GLFWMouseButtonCallback() {
            override fun invoke(window: Long, btn: Int, action: Int, mods: Int) {
                mouseButtons[btn] = action != GLFW.GLFW_RELEASE
                if (btn == GLFW.GLFW_MOUSE_BUTTON_LEFT) w.mouseLocked = true
            }
        }
        scrollListener = object : GLFWScrollCallback() {
            override fun invoke(window: Long, x: Double, y: Double) {
                scrollX += x
                scrollY += y
            }
        }
    }
}