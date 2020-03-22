package posidon.uranium.engine.utils

import java.io.BufferedReader
import java.io.InputStreamReader

object FileUtils {
    fun loadAsString(path: String): String {
        val result = StringBuilder()
        try {
            BufferedReader(InputStreamReader(FileUtils::class.java.getResourceAsStream(path))).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) result.append(line).append("\n")
            }
        } catch (e: Exception) {
            System.err.println("Couldn't find the file $path")
            e.printStackTrace()
        }
        return result.toString()
    }
}