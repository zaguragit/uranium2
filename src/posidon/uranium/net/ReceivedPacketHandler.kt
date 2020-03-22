package posidon.uranium.net

import posidon.uranium.engine.graphics.Renderer
import posidon.library.types.Vec3i
import posidon.library.util.Compressor
import posidon.uranium.engine.objects.Camera
import posidon.uranium.engine.objects.Chunk
import posidon.uranium.main.Globals
import posidon.uranium.main.Main
import java.util.zip.Deflater
import java.util.zip.Inflater

object ReceivedPacketHandler {

    private val blockDictionary = HashMap<Int, String>()

    operator fun invoke(packet: String) {
        val tokens = packet.split('&');
        when (tokens[0]) {
            "time" -> Globals.time = tokens[1].toDouble()
            "pos" -> {
                val coords = tokens[1].split(',')
                Camera.position.set(coords[0].toFloat(), coords[1].toFloat(), coords[2].toFloat())
            }
            "rot" -> {
                val coords = tokens[1].split(',')
                Camera.rotation.set(coords[0].toFloat(), coords[1].toFloat())
            }
            "chunk" -> {
                val blocks = Compressor.decompressString(packet.substring(7 + tokens[1].length))
                val coords = tokens[1].substring(7).split(',')
                val chunkPos = Vec3i(coords[0].toInt(), coords[1].toInt(), coords[2].toInt())
                for (i in 3..blocks.lastIndex step 4) {
                    val material = (blocks[i - 3].toInt() shl 16) or blocks[i - 2].toInt()
                    //println("{ $material -> ${blockDictionary[material] ?: "air"} }")
                    Renderer[Vec3i(
                        (i / 4) / (Chunk.SIZE * Chunk.SIZE),
                        (i / 4) / Chunk.SIZE % Chunk.SIZE,
                        (i / 4) % Chunk.SIZE
                    ), chunkPos] = if (material == -1) "" else blockDictionary[material]!!
                }
            }
            "dict" -> {
                println(tokens[1])
                val defs = tokens[1].split(',')
                for (def in defs) {
                    val eqI = def.indexOf('=')
                    blockDictionary[def.substring(0, eqI).toInt()] = def.substring(eqI + 1)
                }
            }
            "playerInfo" -> {
                for (token in tokens) {
                    when {
                        token.startsWith("coords") -> {
                            val coords = token.substring(7).split(',')
                            Camera.position.x = coords[0].toFloat()
                            Camera.position.y = coords[1].toFloat()
                            Camera.position.z = coords[2].toFloat()
                        }
                        token.startsWith("movSpeed") -> Camera.moveSpeed = token.substring(10).toFloat()
                        token.startsWith("jmpHeight") -> Camera.jumpHeight = token.substring(11).toFloat()
                        token.startsWith("time") -> Globals.time = token.substring(6).toDouble()
                    }
                }
            }
            "" -> Main.running = false
        }
    }
}