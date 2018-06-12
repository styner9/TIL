import java.io.File

fun open(filename: String): File = File(object {}.javaClass.getResource(filename).toURI())

fun inputFilename(array: Array<String>, default: String = "input.txt"): String {
    return if (array.size > 1) {
        array[0]
    } else {
        default
    }
}