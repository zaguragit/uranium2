package posidon.uranium.engine.graphics

object ShadeLang {
    fun shadeToGLSL(text: String): String {
        val builder = StringBuilder("#version 420 core\n")
        val lineBuilder = StringBuilder()
        var parenthesesDepth = 0
        for (char in text) when (char) {
            '\n' -> {
                val line = lineBuilder.toString()
                lineBuilder.clear()
                when {
                    line.replace(" ", "").startsWith("main{") ->
                        builder.append("void " + line.replaceFirst("main", "main()"))
                    line.startsWith("uni ") -> builder.append("uniform" + line.substring(3))
                    else -> builder.append(line)
                }
                if (!(line.endsWith('{') || line.endsWith('}') || line.endsWith('(') ||
                    line.endsWith(';') || line.replace(" ", "").replace("\t", "").isEmpty() || parenthesesDepth != 0)) builder.append(';')
            }
            else -> {
                when (char) {
                    '(' -> parenthesesDepth++
                    ')' -> parenthesesDepth--
                }
                lineBuilder.append(char)
            }
        }
        return builder.toString()
    }
}