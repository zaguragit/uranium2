package posidon.library.util

import java.util.*
import java.util.zip.Deflater
import java.util.zip.Inflater

object Compressor {

    fun compressString(input: String): String {
        //println("ORIGINAL: $input")
        val deflater = Deflater()
        deflater.setInput(input.toByteArray(Charsets.UTF_16))
        deflater.finish()
        val buffer = ByteArray(2048)
        val length = deflater.deflate(buffer)
        deflater.end()
        return Base64.getEncoder().encodeToString(buffer.copyOf(length))//.also { println("COMPRESSED: $it") }
    }

    fun decompressString(input: String): String {
        //println("ORIGINAL: $input")
        val inflater = Inflater()
        inflater.setInput(Base64.getDecoder().decode(input))
        val buffer = ByteArray(32768)
        val length = inflater.inflate(buffer)
        inflater.end()
        return String(buffer, 0, length, Charsets.UTF_16)//.also { println("DECOMPRESSED: $it") }
    }
}