import kotlin.math.min

fun main(args: Array<String>) {

    fun count(wordList: List<String>, stopWords: Set<String>, wordFreqs: MutableMap<String, Int>) {
        if (wordList.isNotEmpty()) {
            val word = wordList[0]
            if (word !in stopWords) {
                wordFreqs[word] = (wordFreqs[word] ?: 0) + 1
            }
            count(wordList.drop(1), stopWords, wordFreqs)
        }
    }

    fun printWordFreqs(i: Int, wordFreqs: List<Pair<String, Int>>) {
        if (wordFreqs.isNotEmpty()) {
            val (w, c) = wordFreqs[0]
            prettyPrint(i, w, c)
            printWordFreqs(i + 1, wordFreqs.drop(1))
        }
    }

    val stopWords = open("stop_words.txt").readText()
            .trim()
            .split(",")
            .toSet()

    val words = open(inputFilename(args, Input.MEDIUM)).readText()
            .trim()
            .toLowerCase()
            .replace("[^a-z]".toRegex(), " ")
            .split(" +".toRegex())
            .filter { it.length >= 2 }

    val wordFreqs = mutableMapOf<String, Int>()

    val recursionLimit = 1000
    for (i in 0 until words.size step recursionLimit) {
        count(words.subList(i, min(i + recursionLimit, words.size)), stopWords, wordFreqs)
    }

    printWordFreqs(1, wordFreqs.toList().sortedByDescending { (_, v) -> v }.take(25))
}