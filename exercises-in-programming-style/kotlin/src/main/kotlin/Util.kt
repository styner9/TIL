import java.io.File

fun open(filename: String): File = File(object {}.javaClass.getResource(filename).toURI())

fun inputFilename(array: Array<String>, default: Input = Input.SHORT): String {
    return if (array.size > 1) {
        array[0]
    } else {
        default.filename
    }
}

enum class Input(val filename: String) {
    SHORT("input.txt"),
    MEDIUM("pride-and-prejudice-head.txt"),
    LONG("pride-and-prejudice.txt");
}

data class MutablePair(var first: String, var second: Int)