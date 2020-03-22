package posidon.uranium.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWWindowSizeCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import posidon.library.types.Matrix4f
import posidon.uranium.main.Globals

object Window {

    private var id: Long = 0
    private var input: Input? = null
    private var resizeListener: GLFWWindowSizeCallback? = null

    var width = 0; private set
    var height = 0; private set
    private lateinit var projection: Matrix4f
    var mouseLocked = false
        set(value) {
            if (value) GLFW.glfwSetCursorPos(id, Input.curX, Input.curY)
            field = value
            GLFW.glfwSetInputMode(id, GLFW.GLFW_CURSOR, if (value) GLFW.GLFW_CURSOR_DISABLED else GLFW.GLFW_CURSOR_NORMAL)
        }

    val projectionMatrix: Matrix4f
        get() = if (Input.isKeyDown(GLFW.GLFW_KEY_C)) Matrix4f.projection(20f, width.toFloat() / height.toFloat(), 0.2f, 200f) else projection

    var isFullscreen = false
        set(fullscreen) {
            if (fullscreen) {
                GLFW.glfwGetWindowPos(id, intArrayOf(pos[0]), intArrayOf(pos[1]))
                val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
                GLFW.glfwSetWindowMonitor(id, GLFW.glfwGetPrimaryMonitor(), 0, 0, videoMode!!.width(), videoMode.height(), 0)
            } else GLFW.glfwSetWindowMonitor(id, 0, pos[0], pos[1], width, height, 0)
            field = fullscreen
        }

    private val pos = IntArray(2)

    fun init(width: Int, height: Int) {
        this.width = width
        this.height = height
        projection = Matrix4f.projection(70f, width.toFloat() / height.toFloat(), 0.2f, 200f)

        if (!GLFW.glfwInit()) {
            System.err.println("[GLFW ERROR]: GLFW wasn't inititalized")
            return
        }
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE)
        input = Input(this)
        id = GLFW.glfwCreateWindow(Window.width, Window.height, title, 0, 0)
        if (id == 0L) {
            System.err.println("[GLFW ERROR]: Window wasn't created")
            return
        }
        val videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
        pos[0] = (videoMode!!.width() - Window.width) / 2
        pos[1] = (videoMode.height() - Window.height) / 2
        GLFW.glfwSetWindowPos(id, pos[0], pos[1])
        GLFW.glfwSetWindowSizeLimits(id, 600, 300, -1, -1)
        GLFW.glfwMakeContextCurrent(id)
        GL.createCapabilities()
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        createCallbacks()
        GLFW.glfwShowWindow(id)
        GLFW.glfwSwapInterval(1)
    }

    private fun createCallbacks() {
        resizeListener = object : GLFWWindowSizeCallback() {
            override fun invoke(window: Long, w: Int, h: Int) {
                width = w
                height = h
                projection = Matrix4f.projection(70f, width.toFloat() / height.toFloat(), 0.2f, 400f)
                GL11.glViewport(0, 0, width, height)
            }
        }
        GLFW.glfwSetKeyCallback(id, input!!.keyListener)
        GLFW.glfwSetCursorPosCallback(id, input!!.cursorListener)
        GLFW.glfwSetMouseButtonCallback(id, input!!.mouseButtonListener)
        GLFW.glfwSetScrollCallback(id, input!!.scrollListener)
        GLFW.glfwSetWindowSizeCallback(id, resizeListener)
    }

    fun update() {
        GL11.glClearColor(Globals.skyColor.x, Globals.skyColor.y, Globals.skyColor.z, 1.0f)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        GLFW.glfwPollEvents()
    }

    fun swapBuffers() = GLFW.glfwSwapBuffers(id)

    val isOpen get() = !GLFW.glfwWindowShouldClose(id)

    fun kill() {
        input!!.kill()
        resizeListener!!.free()
        GLFW.glfwDestroyWindow(id)
        GLFW.glfwTerminate()
    }

    var title: String = "uranium"
        set(value) {
            field = value
            GLFW.glfwSetWindowTitle(id, field)
        }
}