package posidon.uranium.net

import posidon.uranium.main.Main
import posidon.uranium.net.packets.JoinPacket
import posidon.uranium.net.packets.Packet
import java.io.*
import java.net.Socket
import java.net.SocketException

class Client : Runnable {

    override fun run() {
        while (Main.running) {
            try { input.bufferedReader(Charsets.UTF_8).readLine()?.let { ReceivedPacketHandler(it) } ?: Main.kill() }
            catch (e: EOFException) { kill() }
            catch (e: SocketException) { kill() }
            catch (e: StreamCorruptedException) { kill() }
            catch (e: Exception) { e.printStackTrace() }
        }
        kill()
    }

    companion object {
        private lateinit var socket: Socket
        private lateinit var output: OutputStream
        private lateinit var input: InputStream
        private lateinit var writer: OutputStreamWriter
        fun start(ip: String?, port: Int): Boolean {
            return try {
                socket = Socket(ip, port)
                output = socket.getOutputStream()
                input = socket.getInputStream()
                writer = OutputStreamWriter(output, Charsets.UTF_8)
                send(JoinPacket("leoxshn", "w04m58cyp49y59ti5ts9io3k"))
                Thread(Client(), "unraniumClient").start()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                Main.running = false
                System.err.println("[CONNECTION ERROR]: Can't connect to potassium server")
                false
            }
        }

        fun send(packet: Packet) {
            try {
                writer.write(packet.toString())
                writer.write(0x0a)
                writer.flush()
            }
            catch (e: SocketException) { kill() }
            catch (e: Exception) { e.printStackTrace() }
        }

        fun kill() {
            try {
                output.close()
                input.close()
                socket.close()
            } catch (ignore: Exception) {}
        }
    }
}