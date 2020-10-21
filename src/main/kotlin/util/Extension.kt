package util

fun String.readLineWithMessageHeader(): String? {
    println(this)
    return readLine()?.toLowerCase()
}