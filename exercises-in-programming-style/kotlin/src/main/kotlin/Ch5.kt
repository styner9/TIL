fun main(args: Array<String>) {

    fun readFile(filepath: String): String =
            open(filepath).readText().trim()

    fun filterCharsAndNormalize(strData: String) =
            strData.replace("[\\W_]+".toRegex(), " ").toLowerCase()

    fun scan(strData: String): List<String> =
            strData.split(" +".toRegex())

    fun removeStopWords(wordList: List<String>): List<String> {
        val stopWords = open("stop_words.txt").readText()
                .trim()
                .split(",")
                .plus(('a'..'z').map { it.toString() })
        return wordList.filter { it !in stopWords }
    }

    fun frequencies(wordList: List<String>): Map<String, Int> {
        val wordFreqs = mutableMapOf<String, Int>()
        wordList.forEach {
            if (it in wordFreqs) {
                wordFreqs[it] = wordFreqs[it]!! + 1
            } else {
                wordFreqs[it] = 1
            }
        }
        return wordFreqs
    }

    fun sort(wordFreqs: Map<String, Int>) =
            wordFreqs.toList().sortedByDescending { (_, value) -> value }

    fun printAll(wordFreqs: List<Pair<String, Int>>) {
        if (wordFreqs.isNotEmpty()) {
            println("${wordFreqs[0].first} - ${wordFreqs[0].second}")
            printAll(wordFreqs.drop(1))
        }
    }

    readFile(inputFilename(args, Input.MEDIUM))
            .let { filterCharsAndNormalize(it) }
            .let { scan(it) }
            .let { removeStopWords(it) }
            .let { frequencies(it) }
            .let { sort(it) }
            .let { printAll(it.take(25)) }
}