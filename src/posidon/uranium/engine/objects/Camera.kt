package posidon.uranium.engine.objects

import org.lwjgl.glfw.GLFW
import posidon.uranium.net.Client
import posidon.uranium.engine.Input
import posidon.uranium.engine.Window
import posidon.uranium.net.packets.MovPacket
import posidon.library.types.Matrix4f
import posidon.library.types.Vec2f
import posidon.library.types.Vec3f
import posidon.library.types.Vec3i
import kotlin.math.cos
import kotlin.math.sin

object Camera {

    var position = Vec3f(0f, 0f, 0f)
    var rotation = Vec2f(0f, 0f)
    var moveSpeed = 0.5f
    var jumpHeight = 0.5f
    var sensitivity = 0.4f

    var viewMatrix: Matrix4f = Matrix4f.view(position, rotation)

    fun init(position: Vec3f, rotation: Vec2f) {
        this.position = position
        this.rotation = rotation
    }

    fun tick() {
        var packet: MovPacket? = null
        var movX = 0f
        var movZ = 0f
        val keys = booleanArrayOf(
                Input.isKeyDown(GLFW.GLFW_KEY_W),
                Input.isKeyDown(GLFW.GLFW_KEY_S),
                Input.isKeyDown(GLFW.GLFW_KEY_A),
                Input.isKeyDown(GLFW.GLFW_KEY_D))
        if (keys[0]) {
            packet = MovPacket()
            movX = sin(Math.toRadians(rotation.y.toDouble())).toFloat() * moveSpeed
            movZ = cos(Math.toRadians(rotation.y.toDouble())).toFloat() * moveSpeed
            position.x -= movX
            position.z -= movZ
            packet.dir = rotation.y
            packet.keys = keys
        }
        if (keys[1]) {
            if (packet == null) {
                packet = MovPacket()
                movX = sin(Math.toRadians(rotation.y.toDouble())).toFloat() * moveSpeed
                movZ = cos(Math.toRadians(rotation.y.toDouble())).toFloat() * moveSpeed
            }
            position.x += movX
            position.z += movZ
            packet.dir = rotation.y
            packet.keys = keys
        }
        if (keys[2]) {
            if (packet == null) {
                packet = MovPacket()
                movX = sin(Math.toRadians(rotation.y.toDouble())).toFloat() * moveSpeed
                movZ = cos(Math.toRadians(rotation.y.toDouble())).toFloat() * moveSpeed
            }
            position.x -= movZ
            position.z += movX
            packet.dir = rotation.y
            packet.keys = keys
        }
        if (keys[3]) {
            if (packet == null) {
                packet = MovPacket()
                movX = sin(Math.toRadians(rotation.y.toDouble())).toFloat() * moveSpeed
                movZ = cos(Math.toRadians(rotation.y.toDouble())).toFloat() * moveSpeed
            }
            position.x += movZ
            position.z -= movX
            packet.dir = rotation.y
            packet.keys = keys
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
            position.y += jumpHeight
            if (packet == null) packet = MovPacket()
            packet.fly = 1
        }
        if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            position.y -= jumpHeight
            when {
                packet == null -> {
                    packet = MovPacket()
                    packet.fly = -1
                }
                packet.fly != 0 -> packet.fly = 0
                else -> packet.fly = -1
            }
        }
        if (packet != null) {
            Client.send(packet)
            //Window.title = "uranium --- position = $position"
        }
        viewMatrix = Matrix4f.view(position, rotation)
    }

    fun isPositionInFov(position: Vec3i): Boolean {
        val posRelToCam: Vec3i = position - this.position.toVec3i()
        val rotY = Math.toRadians((rotation.y - 180).toDouble())
        val cosRY = cos(rotY)
        val sinRY = sin(rotY)
        val cosRX = cos(Math.toRadians(rotation.x.toDouble()))
        val sinRX = sin(Math.toRadians(rotation.x.toDouble()))
        val x = (posRelToCam.x * cosRY - posRelToCam.z * sinRY) * cosRX + posRelToCam.y * sinRX
        val z = (posRelToCam.z * cosRY + posRelToCam.x * sinRY) * cosRX + posRelToCam.y * sinRX
        val y = posRelToCam.y * cosRX - z * sinRX
        val maxXOffset: Double = z * Window.width / Window.height + Chunk.SIZE * 2
        val maxYOffset = z * cosRX + posRelToCam.y * sinRX + Chunk.SIZE * 2
        return z > -Chunk.SIZE * 2 && x < maxXOffset && x > -maxXOffset && y < maxYOffset && y > -maxYOffset
    }
}