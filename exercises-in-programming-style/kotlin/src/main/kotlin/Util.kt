import java.io.File

fun open(filename: String): File = File(object {}.javaClass.getResource(filename).toURI())

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