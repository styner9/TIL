import java.io.File
import java.io.IOException

fun open(filename: String): File {
    return try {
        File(object {}.javaClass.getResource(filename).toURI())
    } catch (e: Exception) {
        throw when(e) {
            is IOException -> e
            else -> IOException(e)
        }
    }
}

fun inputFilename(array: Array<String>, default: Input = Input.SHORT): String {
    return if (array.size > 1) {
        array[0]
    } else {
        default.filename
    }
}

fun prettyPrint(i: Int, word: String, count: Int) {
    println("[#%02d] %s - %d".format(i, word, count))
}

fun List<Pair<String, Int>>.prettyPrint() {
    forEachIndexed { i, (k, v) ->
        prettyPrint(i + 1, k, v)
    }
}

enum class Input(val filename: String) {
    SHORT("input.txt"),
    MEDIUM("pride-and-prejudice-head.txt"),
    LONG("pride-and-prejudice.txt");
}

data class MutablePair(var first: String, var second: Int)