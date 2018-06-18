typealias Func = (Any, Any) -> Unit

class Ch8 {
    fun readFile(filepath: String, func: Func) {
        open(filepath).readText().trim().let {
            func(it, this::normalize)
        }
    }

    fun filterChars(data: String, func: Func) {
        data.replace("[\\W_]+".toRegex(), " ").let {
            func(it, this::scan)
        }
    }

    fun normalize(data: String, func: Func) {
        data.toLowerCase().let {
            func(it, this::removeStopWords)
        }
    }

    fun scan(data: String, func: Func) {
        data.trim().split(" +".toRegex()).let {
            func(it, this::frequencies)
        }
    }

    fun removeStopWords(wordList: List<String>, func: Func) {
        val stopWords = open("stop_words.txt").readText()
                .trim()
                .split(",")
                .plus(('a'..'z').map { it.toString() })

        wordList.filterNot { it in stopWords }.let {
            func(it, this::sort)
        }
    }

    fun frequencies(wordList: List<String>, func: Func) {
        wordList.groupingBy { it }.eachCount().let {
            func(it, this::printText)
        }
    }

    fun sort(wf: Map<String, Int>, func: Func) {
        wf.toList().sortedByDescending { (_, v) -> v }.toMap().let {
            func(it, this::noOp)
        }
    }

    fun printText(wf: Map<String, Int>, func: (Any) -> Unit) {
        wf.toList().take(25).prettyPrint()
        func(Unit)
    }

    fun noOp(@Suppress("UNUSED_PARAMETER") func: Any) = Unit
}

fun main(args: Array<String>) {
    with(Ch8()) {
        @Suppress("UNCHECKED_CAST")
        readFile(inputFilename(args, Input.MEDIUM), this::filterChars as Func)
    }
}
