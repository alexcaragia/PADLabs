package util

fun readLineWithHeader(header: String): String? {
    println(header)
    return readLine()?.toLowerCase()
}