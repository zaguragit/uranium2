package posidon.library.util

inline fun String.newLineEscape() = replace("\\", "\\\\").replace("\n", "\\n")
inline fun String.newLineUnescape() = replace(Regex("(?<=(?<!\\)\\(\\\\))*\\n"), "\n").replace("\\\\", "\\")