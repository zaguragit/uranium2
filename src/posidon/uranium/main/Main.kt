package posidon.uranium.main

import posidon.uranium.net.Client
import posidon.uranium.engine.Window
import posidon.uranium.engine.graphics.Renderer
import posidon.uranium.engine.graphics.Textures
import posidon.library.types.Vec2f
import posidon.library.types.Vec3f
import posidon.uranium.engine.objects.Camera
import posidon.uranium.engine.ui.HotBar
import posidon.uranium.engine.ui.LoadingScreen
import kotlin.concurrent.thread
import kotlin.system.exitProcess

lateinit var cameraThread: Thread
lateinit var bgThread: Thread

object Main {

    var running = true
    private const val ns = 1000000000 / 60.0

    @JvmStatic
    fun main(args: Array<String>) {
        ////START/////////////////////////////////////
        Window.init(800, 600)
        Renderer.init()
        val loadingScreen = LoadingScreen()
        render()
        if (!Client.start("localhost", 2512)) {
            loadingScreen.setBackgroundPath("res/textures/ui/couldnt_connect.png")
            while (Window.isOpen) render()
            kill()
        }
        Textures.init(null)
        Camera.init(Vec3f(0f, 0f, 0f), Vec2f(0f, 0f))

        cameraThread = thread {
            var lastTime = System.nanoTime()
            var delta = 0.0
            while (running) {
                val now = System.nanoTime()
                delta += (now - lastTime) / ns
                lastTime = now
                while (delta >= 1) { Camera.tick(); delta-- }
            }
        }

        bgThread = thread {
            var lastTime = System.nanoTime()
            var delta = 0.0
            //var ticksSinceRenderIntevention = 0
            while (running) {
                val now = System.nanoTime()
                delta += (now - lastTime) / ns
                lastTime = now
                while (delta >= 1) {
                    /*if (ticksSinceRenderIntevention > 9) {
                        Renderer.bg()
                        ticksSinceRenderIntevention = 0
                    } else ticksSinceRenderIntevention++*/
                    Globals.tick()
                    delta--
                }
            }
        }

        var lastTime = System.nanoTime()
        var delta = 0.0
        loadingScreen.visible = false
        ////GUI///////////////////////////////////////
        HotBar()
        ////LOOP//////////////////////////////////////
        while (Window.isOpen && running) {
            val now = System.nanoTime()
            delta += (now - lastTime) / ns
            lastTime = now
            render()
            if (delta > 9) {
                Renderer.updateData()
                delta = 0.0
            }
        }
        ////END///////////////////////////////////////
        kill()
    }

    private fun render() {
        Window.update()
        Renderer.render()
        Window.swapBuffers()
    }

    fun kill() {
        running = false
        Window.kill()
        Renderer.kill()
        Client.kill()
        exitProcess(0)
    }
}